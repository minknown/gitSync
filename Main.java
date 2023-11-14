package com.mayizt;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {


        //获取用户输入

        System.out.println("Welcome to use it,By minknown.");

        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入Github用户名:");
        String ua = scanner.nextLine();
        System.out.print("请输入Gitee用户名:");
        String ub = scanner.nextLine();
        System.out.println("GiteeToken获取见https://gitee.com/api/v5/swagger");
        System.out.print("请输入Gitee的密钥TOKEN:");
        String token = scanner.nextLine();
        if(token.isEmpty()){
            System.out.println("TOKEN禁止为空,程序无法继续。");
            System.exit(9);
        }else{
            token="?access_token="+token;
            if(ua.isEmpty()){ua="minknown";}
            if(ub.isEmpty()){ub="minknown";}
        }

        System.out.println("-----------------------");
        System.out.println("正在扫描"+ua+"的Github...");
        ArrayList ei=new ArrayList();
        ArrayList eiu=new ArrayList();


        //扫描
        String html=Tools.httpget("https://api.github.com/users/"+ua+"/repos");
        JSONArray jsonArray=JSONUtil.parseArray(html);
        int all=jsonArray.size();
        int i=0;
        if(all<=0){
            System.out.println("[Error]Github上暂无仓库或Github接口错误,程序无法继续。");
            System.out.println("接口位置：接口api.github.com/users/"+ua+"/repos");
            System.exit(9);
        }
        for (Object temp:jsonArray){

           //扫描_获取这个Github仓库的信息
           String status="";
           JSONObject rep = (JSONObject)temp;//rep代表当前仓库的实例。
           String repname=rep.getStr("name");
           String uptime=rep.getStr("pushed_at");

            if(!repname.equals("know") && !repname.equals("cmsAutoApi") && !repname.equals("encoderDemo")){
                //调试用，只检测两个已知仓库。
                //continue;
            }
           //扫描_获取这个GitTee仓库的信息
           html=Tools.httpget("https://gitee.com/api/v5/repos/"+ub+"/"+repname+token);
            if(html.contains("403")){
                System.out.println("[GiteeToken]Gitee接口调用受限,当前仓库名"+repname);
                System.out.println("[GiteeToken]URL:gitee.com/api/v5/repos/"+ub+"/"+repname+token);
                System.exit(9);
            }
           JSONObject rep_gitee=JSONUtil.parseObj(html);
           String rep_giteename=rep_gitee.getStr("name");
           String rep_giteeuptime=rep_gitee.getStr("pushed_at");

           //扫描_检查仓库状态
           if(repname.equals(rep_giteename)){

               Date date1 = DateUtil.parse(uptime);
               Date date2 = DateUtil.parse(rep_giteeuptime);
               if(date1.compareTo(date2)>0){
                   eiu.add(repname);
                   status="未更新[GithubTime:"+uptime+"][GiteeTime:"+rep_giteeuptime+"]";
                   status="未更新";
                   status=Tools.getRedStr(status);
               }else{
                   String pub=rep_gitee.getStr("public");
                   if(!pub.equals("true")){
                       ei.add(repname);
                       status="未设置开源";
                       status=Tools.getPinkStr(status);
                   }else{
                       status="正常";
                   }

               }


           }else{

                   status="未同步创建";
                   status=Tools.getBlueStr(status);

           }

           //输出
            i++;
            System.out.println(i+":"+repname+"=>"+status);
            Thread.sleep(2000);


            //
        }

        System.out.println("------------------");
        System.out.println("扫描完成,总共有" +all+"个仓库在Github上。");


        System.out.println("未同步创建仓库的，可使用Gitee自带的工具进行批量导入，参见下述网址：");
        System.out.println("https://gitee.com/projects/import/github/status");
        System.out.println("键入1回车来打开未开源的Gitee仓库，键入2回车打开未更新的Gitee仓库。");
        System.out.println("注意：为了避免CPU资源不足，我们最多只会打开20个左右。");
        System.out.println("注意：请提前在系统默认浏览器上成功登录您的Gitee账户。");
        System.out.println("请输入操作字符或任意字符以退出程序：");
        int read = System.in.read();
        if(read==49) {
            System.out.println("正在打开仓库网页(未开源)...");
            for (int i1 = 0; i1 < 20; i1++) {
                if(i1>=ei.size()){break;}
                Tools.openurl("https://gitee.com/"+ub+"/"+ei.get(i1)+"/settings#index");
                Thread.sleep(2000);
            }

        }
        if(read==50) {
            System.out.println("正在打开仓库网页(未更新)...");
            for (int i2 = 0; i2 < 20; i2++) {
                if(i2>=eiu.size()){break;}
                Tools.openurl("https://gitee.com/"+ub+"/"+eiu.get(i2)+"/settings#index");
                Thread.sleep(2000);
            }
        }
        System.exit(0);

    }
}