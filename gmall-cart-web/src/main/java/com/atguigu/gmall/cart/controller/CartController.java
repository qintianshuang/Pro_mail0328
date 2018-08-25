package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.annotation.LoginRequire;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Reference
    private SkuService skuService;

    @Reference
    private CartService cartService;


    //复选框状态
    @LoginRequire(ifNeedSuccess = false)
    @RequestMapping("checkCart")
    private String checkCart(HttpServletRequest request, HttpServletResponse response,CartInfo cartInfo, ModelMap map){

        List<CartInfo> cartInfos = new ArrayList<>();
        String userId = (String) request.getAttribute("userId");
        //修改购物车的状态
        if (StringUtils.isBlank(userId)){
            //更新cookie
            //取cookie的数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)){
                cartInfos = JSON.parseArray(cartListCookie,CartInfo.class);
                for (CartInfo info : cartInfos) {
                    if (info.getSkuId().equals(cartInfo.getSkuId())){
                        info.setIsChecked(cartInfo.getIsChecked());
                    }
                }
            }
            CookieUtil.setCookie(request,response,"cartListCookie",JSON.toJSONString(cartInfos),60*60*24*7,true);
        }else {
            //用户已登录
            //更新db缓存
            cartInfo.setUserId(userId);
            cartService.updateCartChecked(cartInfo);
            cartInfos = cartService.getCartCache(userId);
        }
        //更新数据后将最新数据查出
        map.put("cartList",cartInfos);
        map.put("totalPrice",getTotalPrice(cartInfos));
        return "cartListInner";
    }

    /***
     * 到购物车结算页面
     * 显示购物车商品列表
     * @param request
     * @param map
     * @return
     */
    @LoginRequire(ifNeedSuccess = false)
    @RequestMapping("cartList")
    private String cartList(HttpServletRequest request, ModelMap map){
        List<CartInfo> cartInfos = new ArrayList<>();
        String userId = (String) request.getAttribute("userId");

        if (StringUtils.isBlank(userId)){
            //取cookie的数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)){

                cartInfos = JSON.parseArray(cartListCookie,CartInfo.class);
            }
        }else {
            //去缓存数据
            cartInfos = cartService.getCartCache(userId);
        }

        map.put("cartList",cartInfos);
        map.put("totalPrice",getTotalPrice(cartInfos));
        return "cartList";
    }


    private BigDecimal getTotalPrice(List<CartInfo> cartInfos) {
        BigDecimal b = new BigDecimal("0");

        for (CartInfo cartInfo : cartInfos) {
            if (cartInfo.getIsChecked().equals("1")){
                b = b.add(cartInfo.getCartPrice());
            }
        }

        return b;
    }


    @LoginRequire(ifNeedSuccess = false)
    @RequestMapping("CartSuccess")
    public String CartSuccess(){
        return "success";
    }


    /***
     * 放入购物车
     * @param request
     * @param response
     * @param cartInfo
     * @return
     */
    @LoginRequire(ifNeedSuccess = false)
    @RequestMapping("addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response,CartInfo cartInfo){
        String id = cartInfo.getSkuId();
        SkuInfo sku = skuService.getSkuById(id);

        cartInfo.setCartPrice(sku.getPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
        cartInfo.setIsChecked("1");
        cartInfo.setImgUrl(sku.getSkuDefaultImg());
        cartInfo.setSkuPrice(sku.getPrice());
        cartInfo.setSkuName(sku.getSkuName());

        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartInfos = new ArrayList<>();
        if (StringUtils.isBlank(userId)){
            //用户未登录，添加cookies
            String cartListCookieStr = CookieUtil.getCookieValue(request, "cartListCookie", true);
            //cookies是否有值
            if (StringUtils.isBlank(cartListCookieStr)){
                cartInfos.add(cartInfo);
            } else {
                cartInfos = new ArrayList<>();
                cartInfos = JSON.parseArray(cartListCookieStr,CartInfo.class);
                //判断是否新的sku
                boolean b = ifNewCart(cartInfos,cartInfo);

                if (b){
                    //添加
                    cartInfos.add(cartInfo);

                }else {
                    //更新
                    for (CartInfo info : cartInfos) {
                        String skuId = info.getSkuId();

                        //如果id与选中的商品skuId一样
                        if (skuId.equals(cartInfo.getSkuId())){
                            //累计数量
                             info.setSkuNum(info.getSkuNum() + cartInfo.getSkuNum());
                             //累计总金额
                             info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
                        }
                    }
                }
            }
            //操作完成后覆盖cookie
            CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(cartInfos),60*60*24*7,true);
        }else {
            //用户已登录，添加db
            String skuId = cartInfo.getSkuId();
            //
            cartInfo.setUserId(userId);
           CartInfo cartInfoDb = cartService.ifCartExists(cartInfo);

            if (cartInfoDb != null){
                //更新数据库
                cartInfoDb.setSkuNum(cartInfoDb.getSkuNum() + cartInfo.getSkuNum());
                cartInfoDb.setCartPrice(cartInfoDb.getSkuPrice().multiply(new BigDecimal(cartInfoDb.getSkuNum())));
                cartService.updateCart(cartInfoDb);
            }else {
                //插入数据库
                cartService.saveCart(cartInfo);
            }
            //同步缓存
            cartService.syncCache(userId);

        }

        return "redirect:/CartSuccess";
    }


    /***
     * 对比商品skuId是否重复
     * @param cartInfos
     * @param cartInfo
     * @return
     */
    private boolean ifNewCart(List<CartInfo> cartInfos, CartInfo cartInfo) {
        boolean b = true;

        for (CartInfo info : cartInfos) {
            String skuId = info.getSkuId();
            if (skuId.equals(cartInfo.getSkuId())){
                b = false;
            }
        }
        return b;
    }
}
