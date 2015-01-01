This project is a pure java implementation with [Jsoup] API for BUPT [NetAccount] login, management and recharge. It is __NOT__ official.

# Note
* 这仅仅包含"[网络账户系统](http://netaccount.bupt.edu.cn/)"的API,不包含"[校园网络登录](http://gw.bupt.edu.cn/)"的API.
* 暂不支持充值

# API

#### IPLocationMap
The location information is abstracted from John Wong's posts on [BYR-BBS](http://bbs.byr.cn/#!article/NetResources/86919) ans his [python-tool](https://github.com/JohnWong/python-tool/blob/master/IpBupt.py) project.
* `getLocalIPLocationMap()`, return the ip-location hashmap.
* `getLocation(String ip)`, get the location of a specific ip address.

#### NetAccount

###### Settings
* `setCallBack(CallBack callback)`, to set async-processor for the async requirement in Android development. `CallBack` is an interface enables process the HTTP response asynchronously if programmed properly.
* `setTimeout(int seconds)`, to set timeout when sending the force offline request. The default timeout is 10 seconds. You'd better set a larger timeout than the default value.
* `setAutoRefreshIndexPage(boolean auto)`. The default setting of `autoRefresh` field is `true` which means the `refreshIndexPage` function will be automatically invoked whenever the force offline request returns success or failure.

###### Send requests
* `sendLoginRequest()`, to send login request and load login page. The most import response is the `captcha` image(验证码图片). You should store or show the image bytes in `showCaptcha` interface.
* `dologin(String captcha)`, to login. If login succeeds, the `showIpList` interface will be invoked.
* `refreshIndexPage()`, to refresh index page. This page contains online IPs, you can setup a timer to monitor the oneline IPs.
* `forceOffline(String ip)`, to force offline a specific ip.
* `sendQueryRequest(String month)`, to query the user login & logout history in a month. The month format is `yyyy-MM`, for example, '2015-01';
* `sendChargeRequest()`, not available now.
* `postChargeForm(int payamount, String captcha)`, not available now.

#### CallBack
This is an interface should be used with `NetAccount` instance.
* `showCaptcha(byte[] captcha, Action action)`, to process captcha caused by a certain request action.
* `showError(String error, Action action)`, to process errors or exception.
* `showIpList(List<String> ipList)`, to show online IPs.
* `showForceOfflineResult(String msg)`, to show the result of force offline action.
* `showQueryResult(LoginLog log)`, to show the logs of user login & logout.
* `chargeSucceed(String balance)`, not available now.

[Jsoup]:http://jsoup.org/
[NetAccount]:http://netaccount.bupt.edu.cn/