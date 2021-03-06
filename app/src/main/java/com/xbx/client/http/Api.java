package com.xbx.client.http;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.android.volley.VolleyError;
import com.baidu.mapapi.model.LatLng;
import com.xbx.client.R;
import com.xbx.client.beans.ReservatInfoBean;
import com.xbx.client.beans.UserInfo;
import com.xbx.client.jsonparse.UserInfoParse;
import com.xbx.client.jsonparse.UtilParse;
import com.xbx.client.linsener.RequestBackLisener;
import com.xbx.client.ui.activity.LoginActivity;
import com.xbx.client.utils.Constant;
import com.xbx.client.utils.SharePrefer;
import com.xbx.client.utils.TaskFlag;
import com.xbx.client.utils.Util;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by EricYuan on 2016/4/5.
 */
public class Api {
    private Handler mHandler;
    private Context context;

    boolean isFirst = true;

    public Api(Context context, Handler mHandler) {
        this.context = context;
        this.mHandler = mHandler;
    }

    /**
     * 检查登录的状态
     */
    public void checkLoginState() {
        String postUrl = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_Login));
        RequestParams params = new RequestParams();
        UserInfo userInfo = SharePrefer.getUserInfo(context);
        final String pushId = JPushInterface.getRegistrationID(context);
        params.put("mobile", userInfo.getUserPhone());
        params.put("password", userInfo.getLoginToken());
        params.put("user_type", "0");//代表用户端
        params.put("push_id", pushId);//代表用户端
        Util.pLog("Login phone=" + userInfo.getUserPhone() + " token:" + userInfo.getLoginToken()+" pushId:"+pushId);
        IRequest.post(context, postUrl, params, new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("Login check Result:" + json);
                Message msg = mHandler.obtainMessage();
                msg.obj = json;
                msg.what = TaskFlag.PAGEREQUESTWO;
                mHandler.sendMessage(msg);
            }

            @Override
            public void requestError(VolleyError e) {
            }
        });
    }

    /**
     * 获取附近的导游
     */
    public void getNearGuide(LatLng currentLalng, String nearGuideUrl, final String guideType) {
        if (currentLalng == null)
            return;
        if (context == null)
            return;
        RequestParams params = new RequestParams();
        params.put("lon", currentLalng.longitude + "");
        params.put("lat", currentLalng.latitude + "");
        params.put("type", guideType);
        IRequest.post(context, nearGuideUrl, params, new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                super.requestSuccess(json);
                if (guideType.equals("1"))
                    Util.pLog("导游列表:" + json);
                else
                    Util.pLog("土著列表:" + json);
                if (isFirst) {
                    isFirst = false;
                }
                sendMsg(TaskFlag.REQUESTSUCCESS, json);
            }

            @Override
            public void requestError(VolleyError e) {
                //该接口不用回调错误信息
            }
        });
    }

    /**
     * 地图上显示为我服务的导游信息
     */
    public void getMyGuideInfo(String orderNum) {
        if (context == null)
            return;
        String guideInfoUrl = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_getMyGuideInfo)).concat("?order_number=" + orderNum);
        IRequest.get(context, guideInfoUrl, new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                super.requestSuccess(json);
//                Util.pLog("服务我的导游:" + json);
                sendShowMsg(TaskFlag.PAGEREQUESTWO, json);
            }

            @Override
            public void requestError(VolleyError e) {
            }
        });
    }

    /**
     * 出行人数选择
     */
    public void getSetoffNum() {
        if (context == null)
            return;
        String setoffNumUrl = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_setoffNum));
        IRequest.get(context, setoffNumUrl, "", new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                super.requestSuccess(json);
                Util.pLog("出行人数选择:" + json);
                sendShowMsg(TaskFlag.PAGEREQUESFIVE, json);
            }
        });
    }

    /**
     * 即时服务下单
     *
     * @param uid
     * @param startInfo 用户开始地址Json
     * @param startInfo
     * @param guideType 导游或者伴游和土著
     * @param guideId   导游Id
     */
    public void hasGuide(String uid, String startInfo, String guideType, String realname, String idcard, String guideId) {
        if (context == null)
            return;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("server_addr_lnglat", startInfo);
        params.put("guide_type", guideType);
        params.put("realname", realname);
        params.put("idcard", idcard);
        params.put("guid", guideId);
        Util.pLog("用户:" + uid + " 用户目的地:" + startInfo + " 导游类型:" + guideType + " 导游id:" + guideId);
        String findUrl = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_immediaGuide));
        IRequest.post(context, findUrl, params, "", new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("是否有导游:" + json);
                sendShowMsg(TaskFlag.PAGEREQUESTHREE, json);
            }

            @Override
            public void requestError(VolleyError e) {
                super.requestError(e);
                mHandler.sendEmptyMessage(TaskFlag.HTTPERROR);
            }
        });
    }

    public void isFindGuide(String uid, String orderNum) {
        if (context == null)
            return;
        String isFindUrl = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_isFindGuide)).concat("?uid=" + uid).concat("&order_number=" + orderNum);
        IRequest.get(context, isFindUrl, new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("是否匹配到了导游:" + json);
                sendMsg(TaskFlag.PAGEREQUESFOUR, json);
            }

            @Override
            public void requestError(VolleyError e) {
                mHandler.sendEmptyMessage(TaskFlag.HTTPERROR);
            }
        });
    }

    /**
     * 取消订单
     *
     * @param orderNum
     */
    public void cancelOrder(String orderNum) {
        if (context == null)
            return;
        String isFindUrl = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_cancelOrder)).concat("?order_number=" + orderNum);
        IRequest.get(context, isFindUrl, "", new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("取消订单:" + json);
                sendAllDatas(TaskFlag.PAGEREQUESFIVE, json);
            }
        });
    }

    /**
     * 预约导游
     *
     * @param uid
     * @param guideId
     * @param cityId
     * @param destination
     * @param startTime
     * @param endTime
     * @param guideType
     */
    public void reservatGuide(String uid, String guideId, String cityId, String destination, String startTime, String endTime, String guideType) {
        if (context == null)
            return;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("guid", guideId);
        params.put("server_city_id", cityId);
        params.put("server_addr_lnglat", destination);
        params.put("server_start_time", startTime);
        params.put("server_end_time", endTime);
        params.put("guide_type", guideType);
        Util.pLog("用户:" + uid + " 用户预约目的:" + destination + "导游类型：" + guideType + " 开始时间:" + startTime + " 导游id:" + guideId + " 城市id:" + cityId);
        String findUrl = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_reservateGuide));
        IRequest.post(context, findUrl, params, "", new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("预约该导游:" + json);
                sendShowMsg(TaskFlag.PAGEREQUESTHREE, json);
            }
        });
    }

    /**
     * 用户订单列表
     *
     * @param uid
     */
    public void getMyOrderList(String uid, boolean isShow) {
        if (context == null)
            return;
        String orderListUrl = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_orderList)).concat("?uid=" + uid + "&now_page=1" + "&page_number=250");
        IRequest.get(context, orderListUrl, isShow, new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                super.requestSuccess(json);
                Util.pLog("订单列表:" + json);
                sendShowMsg(TaskFlag.REQUESTSUCCESS, json);
            }
        });
    }

    public void getOrderDetail(String orderNum) {
        if (context == null)
            return;
        String orderDetailUrl = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_orderDetail)).concat("?order_number=" + orderNum);
        Util.pLog("orderDetailUrl:" + orderDetailUrl);
        IRequest.get(context, orderDetailUrl, "", new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                super.requestSuccess(json);
                Util.pLog("订单详情:" + json);
                sendShowMsg(TaskFlag.REQUESTSUCCESS, json);
            }
        });
    }

    public void toUploadLatlng(String url, String uid, String lon, String lat) {
        if (context == null)
            return;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("lon", lon);
        params.put("lat", lat);
        IRequest.post(context, url, params, new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
//                Util.pLog("上传经纬度:" + json);
            }

            @Override
            public void requestError(VolleyError e) {
            }
        });
    }

    /**
     * 获取预约导游列表
     *
     * @param reservatBean
     * @param pageIndex
     * @param pageNum
     */
    public void getReserveGuideList(ReservatInfoBean reservatBean, String pageIndex, String pageNum, final int requestFlag) {
        if (context == null)
            return;
        String paramsData = "?server_city_id=" + reservatBean.getCityId() + "&server_addr_lnglat=" + reservatBean.getAddress() + "&sex=" + reservatBean.getSexType()
                + "&lang_type=" + reservatBean.getLanguageType() + "&server_start_time=" + reservatBean.getStartTime() + "&server_end_time=" + reservatBean.getEndTime()
                + "&guide_type=" + reservatBean.getGuideType() + "&now_page=" + pageIndex + "&page_number=" + pageNum;
        String url = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_guideList)).concat(paramsData);
        Util.pLog(url);
        IRequest.get(context, url, "", new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("预约导游列表:" + json);
                sendShowMsg(requestFlag, json);
            }

            @Override
            public void requestError(VolleyError e) {
                super.requestError(e);
                mHandler.sendEmptyMessage(TaskFlag.REQUESTERROR);
            }
        });
    }

    /**
     * 导游详情
     *
     * @param guideId
     */
    public void getGuideDetail(String guideId) {
        if (context == null)
            return;
        String url = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_guideDetail)).concat("?uid=" + guideId);
        IRequest.get(context, url, "", new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("导游详情:" + json);
                sendShowMsg(TaskFlag.REQUESTSUCCESS, json);
            }
        });
    }

    /**
     * 评论的标签
     */
    public void getGuideTag() {
        if (context == null)
            return;
        String url = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_guideTag));
        IRequest.get(context, url, "", new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("导游评论标签:" + json);
                sendShowMsg(TaskFlag.PAGEREQUESTWO, json);
            }
        });
    }

    public void submitComment(String uid, String orderNum, String content, String star, String guideTag) {
        if (context == null)
            return;
        RequestParams params = new RequestParams();
        params.put("uuid", uid);
        params.put("order_number", orderNum);
        params.put("content", content);
        params.put("star", star);
        params.put("tag", guideTag);
        params.inputParams();
        String url = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_submitComment));
        IRequest.post(context, url, params, "", new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("提交评论:" + json);
                sendShowMsg(TaskFlag.PAGEREQUESTHREE, json);
            }
        });
    }

    /**
     * 修改个人信息
     *
     * @param uid
     * @param realname
     * @param sex
     * @param idcard
     * @param nickname
     * @param birthday
     */
    public void modifyInfo(String uid, String realname, String sex, String idcard, String nickname, String birthday, File file) {
        if (context == null)
            return;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("realname", realname);
        params.put("sex", sex);
        params.put("idcard", idcard);
        params.put("nickname", nickname);
        params.put("birthday", birthday);
        if (file != null)
            params.put("head_image", file);
        String url = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_modifyHead));
        IRequest.post(context, url, params, new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("修改个人信息:" + json);
                sendShowMsg(TaskFlag.REQUESTSUCCESS, json);
            }
        });
    }

    public void modifyHead(String uid, File headFile, String birthday) {
        if (context == null)
            return;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("head_image", headFile);
        params.put("birthday", birthday);
        params.put("now_address", "");
        params.put("server_language", "");
        String url = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_modifyHead));
        IRequest.post(context, url, params, new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("修改头像:" + json);
                sendShowMsg(TaskFlag.PAGEREQUESTWO, json);
            }
        });
    }

    public void toFeedback(String uid, String content) {
        if (context == null)
            return;
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("content", content);
        String url = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_feedback));
        IRequest.post(context, url, params, new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("意见反馈:" + json);
                sendShowMsg(TaskFlag.REQUESTSUCCESS, json);
            }
        });
    }

    public void checkUpdate() {
        if (context == null)
            return;
        String url = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_updateUrl));
        IRequest.get(context, url, new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("版本更新:" + json);
                sendShowMsg(TaskFlag.PAGEREQUESFOUR, json);
            }
        });
    }

    public void getNearTogether(String togetherLnlat, String guideType) {
        if (context == null)
            return;
        String url = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_nearTogether)).concat("?server_addr_lnglat=" + togetherLnlat);
        Util.pLog("周边随游url:" + url);
        IRequest.get(context, url, new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("获取周边随游:" + json);
                sendShowMsg(TaskFlag.REQUESTSUCCESS, json);
            }

            @Override
            public void requestError(VolleyError e) {
            }
        });
    }

    public void moniPayOrder(String orderNum) {
        if (context == null)
            return;
        String url = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_payOrder)).concat("?order_number=" + orderNum);
        Util.pLog("支付接口：" + url);
        IRequest.get(context, url, "", new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("支付结果:" + json);
                sendShowMsg(TaskFlag.PAGEREQUESFIVE, json);
            }
        });
    }

    public void getPayInfo(String orderNum, String payWay) {
        if (context == null)
            return;
        RequestParams params = new RequestParams();
        params.put("order_number", orderNum);
        params.put("pay_channel", payWay);
        String url = context.getString(R.string.url_conIp).concat(context.getString(R.string.url_pays));
        IRequest.post(context, url, params, "", new RequestBackLisener(context) {
            @Override
            public void requestSuccess(String json) {
                Util.pLog("支付信息:" + json);
                sendShowMsg(TaskFlag.PAGEREQUESFOUR, json);
            }
        });
    }

    /**
     * 无判断code=0的情况
     */
    private void sendMsg(int flag, String json) {
        Message msg = mHandler.obtainMessage();
        if (UtilParse.getRequestCode(json) == 1) {
            msg.obj = UtilParse.getRequestData(json);
            msg.what = flag;
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 有判断code=0的情况
     */
    private void sendShowMsg(int flag, String json) {
        Message msg = mHandler.obtainMessage();
        if (UtilParse.getRequestCode(json) == 1) {
            msg.obj = UtilParse.getRequestData(json);
            msg.what = flag;
            mHandler.sendMessage(msg);
        } else {
            mHandler.sendEmptyMessage(TaskFlag.CODEZERO);
            String showMsg = UtilParse.getRequestMsg(json);
            if (!Util.isNull(showMsg))
                Util.showToast(context, showMsg);
        }
    }

    private void sendAllDatas(int flag, String json) {
        Message msg = mHandler.obtainMessage();
        msg.obj = json;
        msg.what = flag;
        mHandler.sendMessage(msg);
    }
}
