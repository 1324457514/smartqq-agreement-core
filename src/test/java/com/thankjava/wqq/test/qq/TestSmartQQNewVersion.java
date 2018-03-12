package com.thankjava.wqq.test.qq;

import com.thankjava.toolkit3d.fastjson.FastJson;
import com.thankjava.wqq.SmartQQClient;
import com.thankjava.wqq.SmartQQClientBuilder;
import com.thankjava.wqq.entity.enums.LoginResultStatus;
import com.thankjava.wqq.entity.sys.LoginResult;
import com.thankjava.wqq.extend.ActionListener;
import com.thankjava.wqq.extend.CallBackListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 新版本SmartQQClient测试代码 version >= 1.1.x
 *
 * @author acexy
 */
public class TestSmartQQNewVersion {

    private static final Logger logger = LoggerFactory.getLogger(TestSmartQQNewVersion.class);

    public static void main(String[] args) {

        /**
         * step 1 > 利用指定使用SmartQQClientBuilder指南来构建SmartQQClient实例
         */
        SmartQQClientBuilder builder = SmartQQClientBuilder.custom(

                // 注册一个通知事件的处理器，它将在SmartQQClient获得到相关信息时被调用执行
                new MessageHandler()
        );


        /**
         * step 2 > 自定义可选参数(为方便查看可选方法，设置参数的函数均以set关键字命名开始)
         */
        builder
                .setAutoGetInfoAfterLogin() // 设置登录成功后立即拉取一些信息
                .setExceptionRetryMaxTimes(3) // 设置如果请求异常重试3次
                .setAutoRefreshQrcode() // 设置若发现登录二维码过期则自动重新拉取
        ;

        /**
         * step 3 > create SmartQQClient 实例 并进行登录
         */

        // A: 声明一个获取到登录二维码的回调函数，将返回二维码的byte数组数据
        CallBackListener getQrListener = new CallBackListener() {

            // login 接口在得到登录二维码时会调用CallBackListener
            // 并且二维码byte[] 数据会通过ListenerAction.data返回

            @Override
            public void onListener(ActionListener actionListener) {

                try {
                    // 将返回的byte[]数据io处理成一张png图片
                    // 位于项目log/qrcode.png
                    ImageIO.write((BufferedImage) actionListener.getData(), "png", new File("./log/qrcode.png"));
                    logger.debug("获取登录二维码完成,手机QQ扫描 ./log/qrcode.png 位置的二维码图片");
                } catch (Exception e) {
                    logger.error("将byte[]写为图片失败", e);
                }

            }
        };
        
        // B: 声明一个登录结果的函数回调，在登录成功或者失败或异常时进行回调触发
        CallBackListener loginListener = new CallBackListener() {

            // ListenerAction.data 返回登录结果 com.thankjava.wqq.entity.enums.LoginResult
            @Override
            public void onListener(ActionListener actionListener) {
            	LoginResult loginResult = (LoginResult) actionListener.getData();
                System.out.println("登录结果: " + loginResult.getLoginStatus());
                if (loginResult.getLoginStatus() == LoginResultStatus.success) {
                	
                	SmartQQClient smartQQClient = loginResult.getClient();

                	// TODO: 后续就可以利用smartQQClient调用API
                    System.out.println(FastJson.toJSONString(smartQQClient.getFriendsList(false)));
                    
                }
            }
        };

        // C: 进行登录动作
        builder.createAndLogin(getQrListener, loginListener);
    }

}
