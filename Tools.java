package com.mayizt;

import cn.hutool.http.HttpUtil;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class Tools {

    //get访问
    public static String httpget(String url){
        String res="";
        Integer jc=0;
        while(res.isEmpty()){
            try{
                res= HttpUtil.get(url,30000);
            }catch (Exception e){
                res="";
                jc++;
                //Tools.printInRed("[httpget]["+jc+"]["+url+"]Connection reset,try agant...")
                if(jc>6){
                    System.out.println(Tools.getRedStr("[httpget]您的网络可能存在问题..."));
                }
                try{Thread.sleep(3000);}catch (Exception ee){}
            }
        }
        return res;
    }
    //打印红色内容
    public static String getRedStr(String txt){
       return "\033[31m"+txt+"\033[0m";
    }
    public static String getBlueStr(String txt){
        return "\033[34m"+txt+"\033[0m";
    }
    public static String getPinkStr(String txt){
        return "\033[35m"+txt+"\033[0m";
    }
    //调用默认浏览器打开网页
    public static void openurl(String url){

        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                URI uri = new URI(url);
                desktop.browse(uri);
            }
        }catch (Exception e) {

        }

    }

}
