package com.shizijie.beta.auth.web;

import com.shizijie.beta.annotation.Lock;
import com.shizijie.beta.auth.dao.UserDao;
import com.shizijie.beta.auth.serivce.SparkTestService;
import com.shizijie.beta.auth.serivce.impl.UserServiceImpl;
import com.shizijie.beta.bean.id.IdWorker;
import com.shizijie.beta.bean.port.ServicePort;
import com.shizijie.beta.bean.user.dto.UserDTO;
import com.shizijie.beta.params.BetaParams;
import com.shizijie.beta.utils.MD5Util;
import com.shizijie.beta.auth.vo.UserVO;
import com.shizijie.beta.model.ResultBean;
import com.shizijie.beta.utils.id.SnowFlakeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author shizijie
 * @version 2018-12-06 下午8:33
 */
@Api(value="user", tags="用户模块")
@RestController
@RequestMapping("/user")
public class LoginController {
    @Autowired
    UserServiceImpl userServiceImpl;
    @Autowired
    UserDao userDao;

//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;

    @ApiOperation(value="用户登录",notes="根据用户信息登录验证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String")
    })
    @PostMapping("/login")
    public ResultBean login(@Validated @RequestBody UserVO vo){
        return userServiceImpl.userLogin(vo.getUserName(), MD5Util.md5(vo.getPassword()));
    }
    @GetMapping("/test/{token}")
    public ResultBean test(@PathVariable String token){
        System.out.println(token);
        //kafkaTemplate.send("xlkafka","hellow!");
//        Task task=new Task();
//        for(int i=0;i<5;i++){
//            new Thread(task).start();
//        }
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return ResultBean.success("1111");
        UserDTO user=new UserDTO();
        user.setUserName("test");
        user.setPassword("2222");
        user.setUserId(SnowFlakeUtils.nextId()+"");
        //userDao.insertUser(user);
        System.out.println("ok");
        return ResultBean.success();
    }
    public ResultBean upload(@RequestParam(value="file",required=true) MultipartFile file, HttpServletRequest request) throws Exception{
        String filePath = null;
        String fileName = file.getOriginalFilename();
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf(".") + 1);
        //检查文件格式是否合法
        if(file.getContentType().toLowerCase().contains("image/") && !StringUtils.isBlank(suffixName)
                && (suffixName.equalsIgnoreCase("BMP")
                || suffixName.equalsIgnoreCase("JPG") || suffixName.equalsIgnoreCase("JPEG")
                || suffixName.equalsIgnoreCase("PNG"))) {
            // 获取登录用户信息
            Integer userId = 1;

            // 设置文件存储路径
            filePath = request.getSession().getServletContext().getRealPath("/") + File.separator + userId + "_" + System.currentTimeMillis() + "." + suffixName;
            byte[] bytes = file.getBytes();
            Path path = Paths.get(filePath);
            //保存在本地
            Files.write(path, bytes);

            //上传到远程文件服务器
            //TODO

            return ResultBean.success(filePath );
        } else {
            return ResultBean.fail( "图片格式错误");
        }
    }


    @Autowired
    private SparkTestService sparkTestService;

    @RequestMapping("/demo/top10")
    public Map<String, Object> calculateTopTen() {
        return sparkTestService.calculateTopTen();
    }

    @RequestMapping("/demo/exercise")
    public void exercise() {
        sparkTestService.sparkExerciseDemo();
    }

    @RequestMapping("/demo/stream")
    public void streamingDemo() throws InterruptedException {
        sparkTestService.sparkStreaming();
    }
}
