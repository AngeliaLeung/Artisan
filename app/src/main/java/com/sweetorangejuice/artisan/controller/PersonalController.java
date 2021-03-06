package com.sweetorangejuice.artisan.controller;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.sweetorangejuice.artisan.base.ArtisanApplication;
import com.sweetorangejuice.artisan.base.BaseActivity;
import com.sweetorangejuice.artisan.model.PersonalBean;
import com.sweetorangejuice.artisan.util.LogUtil;
import com.sweetorangejuice.artisan.view.Activity.LoginActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by as on 2017/5/5.
 */

public class PersonalController {
    /**
     * 退出登录
     * @param currentUser
     */
    public static void logOut(AVUser currentUser) {
        if (currentUser != null) {
            LoginController.LogOut();
        }
        BaseActivity.finishAll();
        LoginActivity.startAction(ArtisanApplication.getContext());
    }

    /**
     * 用户在注册的时候生成相应个人资料表
     * @param username
     */
    public static void createPersonalInfo(String username){
        AVObject personalObject = new AVObject("Person");
        personalObject.put("username", username);
        LoginController.currentInfo = personalObject;
        personalObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                LoginController.taskFinished += 1;
                if(e == null) {
                    LoginController.checkState();
                }else{
                    LogUtil.d("LoginController","info failed");
                    LoginController.SignUpState = LoginController.State.FAILED;
                    LoginController.checkState();
                }
            }
        });
    }

    /**
     * 用户获取个人资料
     * @return
     */
    public static AVObject showPersonalInfo() {
        AVQuery<AVObject> avQuery = new AVQuery<>("Person");
        avQuery.whereEqualTo("username", AVUser.getCurrentUser().getUsername());
        AVObject personalObject = null;
        try {
            personalObject = avQuery.getFirst();
            Log.d("TAG",""+(personalObject==null)+","+AVUser.getCurrentUser().getUsername());
        } catch (AVException e) {
            if (e == null) {
                return personalObject;
            } else {
                return null;
            }
        }
        return null;
    }

    public static void UploadheadImage(String objectId){
        try {
            String PersonObjectIdCql = "select objectId from Person where username = " + AVUser.getCurrentUser().getUsername();
            AVObject personalObject = AVQuery.doCloudQuery(PersonObjectIdCql).getResults().get(0);
            String ImageUpdateCql = "update Person set headImage = '"+ objectId +"' where objectId = '"
                    +(String)personalObject.get("objectId")+"'";
            AVQuery.doCloudQuery(ImageUpdateCql);
        }catch(Exception ex){
        }
    }

    /**
     * 传入已经修改好的personalBean
     * 用于更新个人资料
     * @param personalBean
     */
    public static void modifyPersonalInfo(PersonalBean personalBean) {
        AVQuery<AVObject> avQuery = new AVQuery<>("Person");
        avQuery.whereEqualTo("username", AVUser.getCurrentUser().getUsername());
        AVObject personalObject = null;
        try {
            personalObject = avQuery.getFirst();
            personalObject.put("gender", personalBean.getGender());
            personalObject.put("age", personalBean.getAge());
            personalObject.put("school", personalBean.getSchool());
            personalObject.put("tag", personalBean.getTag());
            //personalObject.put("headImage", AVFile.withAbsoluteLocalPath("headImg.jpg", personalBean.getHeadImage()));
            personalObject.saveInBackground();
            Log.d("TAG",""+(personalObject==null)+","+AVUser.getCurrentUser().getUsername());
        } catch (AVException e) {
            if (e == null) {

            } else {

            }
        }

    }

    /**
     * 我的发布
     *      返回对应数量的朋友圈列表
     * @param limit 当前需要获取的朋友圈数量
     * @param skip  跳过前方多少个朋友圈
     * @return
     */
    public static List<String> getMyMoments(int limit,int skip)
    {
        AVQuery<AVObject> query = new AVQuery<>("Moments");
        query.whereEqualTo("author",AVUser.getCurrentUser().getUsername());
        query.limit(limit);
        query.skip(skip);
        query.addDescendingOrder("createdAt");
        List<AVObject> temp;
        List<String> result = new ArrayList<>();
        try {
            temp =  query.find();
            for(AVObject moment:temp){
                result.add(moment.getObjectId());
            }
        }catch(AVException e){
            e.printStackTrace();
        }
        return result;
    }
    public static List<String> getMyCollection(int limit,int skip)
    {
        AVQuery<AVObject> query = new AVQuery<>("Collection");
        query.whereEqualTo("username",AVUser.getCurrentUser().getUsername());
        query.limit(limit);
        query.skip(skip);
        query.addDescendingOrder("createdAt");
        List<AVObject> temp;
        List<String> result = new ArrayList<>();
        try {
            temp =  query.find();
            for(AVObject moment:temp){
                result.add((String) moment.get("momentsId"));
            }
        }catch(AVException e){
            e.printStackTrace();
        }
        return result;
    }

}
