package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Autowired
    private RedisUtil redisUtil;

    /***
     * 登录情况下
     * 通过用户id和商品id
     * 查询出购物车内的商品
     * @param cartInfo
     * @return
     */
    @Override
    public CartInfo ifCartExists(CartInfo cartInfo) {

        CartInfo cartInfo1 = new CartInfo();
        cartInfo1.setUserId(cartInfo.getUserId());
        cartInfo1.setSkuId(cartInfo.getSkuId());
        CartInfo select = cartInfoMapper.selectOne(cartInfo1);

        return select;
    }

    @Override
    public void updateCart(CartInfo cartInfoDb) {

        cartInfoMapper.updateByPrimaryKeySelective(cartInfoDb);
    }

    @Override
    public void saveCart(CartInfo cartInfo) {
        cartInfoMapper.insertSelective(cartInfo);
    }

    /***
     * 登录情况下
     * 同步缓存(用户，商品)
     * @param userId
     */
    @Override
    public void syncCache(String userId) {
        Jedis jedis = redisUtil.getJedis();
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> select = cartInfoMapper.select(cartInfo);

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        if (select == null || select.size() == 0){
            jedis.del("carts:" + userId + ":info");
        }else {
            for (CartInfo info : select) {
                stringStringHashMap.put(info.getId(), JSON.toJSONString(info));
            }
            jedis.hmset("carts:" + userId + ":info",stringStringHashMap);
        }
        jedis.close();
    }

    /***
     *
     * @param userId
     * @return
     */
    @Override
    public  List<CartInfo> getCartCache(String userId) {
        List<CartInfo> cartInfos = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals("carts:" + userId + ":info");

        if (hvals != null && hvals.size() > 0) {
            for (String hval : hvals) {
                CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
                cartInfos.add(cartInfo);
            }
        }
        return cartInfos;
    }

    @Override
    public void updateCartChecked(CartInfo cartInfo) {

        Example e  = new Example(CartInfo.class);
        e.createCriteria().andEqualTo("skuId",cartInfo.getSkuId()).andEqualTo("userId",cartInfo.getUserId());
        cartInfoMapper.updateByExampleSelective(cartInfo,e);
        syncCache(cartInfo.getUserId());
    }

    /***
     *提交购物车订单
     * 进入确认订单详情
     * @param cartInfos
     * @param userId
     */
    @Override
    public void combineCart(List<CartInfo> cartInfos, String userId) {
        if (cartInfos != null){
            for (CartInfo cartInfo : cartInfos) {
                CartInfo info = ifCartExists(cartInfo);

                if (info == null){
                    //如果数据库没有这条购物车信息，就直接添加
                    cartInfo.setUserId(userId);
                    cartInfoMapper.insertSelective(cartInfo);
                }else {
                    //如果数据库有这条购物车信息，就直接更新数据
                    info.setSkuNum(cartInfo.getSkuNum() + info.getSkuNum());
                    info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                    cartInfoMapper.updateByPrimaryKeySelective(info);
                }
            }
        }
        //同步缓存
        syncCache(userId);
    }

    /***
     * 将购物车选中的商品确认
     * 然后提交生成订单
     * 提交订单后删除购物车数据
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartCacheByCheck(String userId) {
        List<CartInfo> cartInfos = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals("carts:" + userId + ":info");

        if (hvals != null && hvals.size() > 0) {
            for (String hval : hvals) {
                CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
                if (cartInfo.getIsChecked().equals("1")){

                    cartInfos.add(cartInfo);
                }
            }
        }

        return cartInfos;
    }


    @Override
    public void deleteCartById(List<CartInfo> cartInfos) {

        //循环删除购物车信息
        for (CartInfo cartInfo : cartInfos) {
            cartInfoMapper.deleteByPrimaryKey(cartInfo);
        }

        //同步缓存
        syncCache(cartInfos.get(0).getUserId());
    }
}
