package cn.edu.xmu.oomall.order.util;

import java.text.SimpleDateFormat;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/24 17:18
 */
public class IdUtil {
    /**
     * 20位末尾的数字id
     */
    public static int Guid=100;

    public static String getGuid() {
        IdUtil.Guid+=1;
        long now = System.currentTimeMillis();
        //获取4位年份数字
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");
        //获取时间戳
        String time=dateFormat.format(now);
        String info=now+"";
        //获取三位随机数
        //int ran=(int) ((Math.random()*9+1)*100);
        //要是一段时间内的数据连过大会有重复的情况，所以做以下修改
        int ran=0;
        if(IdUtil.Guid>999){
            IdUtil.Guid=100;
        }
        ran=IdUtil.Guid;
        return time+info.substring(2, info.length())+ran;
    }
}
