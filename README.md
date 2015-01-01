This project is a pure java implementation with [Jsoup] API for BUPT wifi [gateway] login, management and recharge. It is __NOT__ official.

# Note
从2015年1月1日00：00正式收费后，网关充模拟值页面已无法访问，目前（2015年1月1日）只能通过学校圈存机进行充值。
"网络账户系统"提供了在线IP管理和上网详单查询，有取代老旧的"用户自助服务系"的趋势。老系统目前还能用，对老系统的功能感兴趣的朋友可以使用此API。对新系统感兴趣的朋友请看[master](https://github.com/KunsLand/NetAccount/)分支。
这里的API仅仅包含"用户自助服务系统"和"网络账户系统"登录和模拟充值（登录还可用，模拟充值功能在2015年1月1日后不可用）的API,不包含"校园网络登录"的API。


# API

#### GWSelf
* `login(String account, String password)`, to login wifi gateway.
* `getInformation()`, get account information including: `leftmoeny`, `onlinestate`, `status`, `welcome`, etc.
* `getOnlineIps()`, get all online IPs.
* `forceOffline(String ip)`, force offline the specified ip.
* `getLoginLog(LogType type)`, get the user login and logout logs. The logs contains `loginTime`, `logoutTime`, `minutes`, `fees`, `upload`, `download`, `ip`, etc. There are two types of logs now, `DAY` (means current date) and `Month` (means current month).
* `getLoginLog(String startDate, String endDate)`, get user login and logout logs during a specified time. The time format is "yyyy-MM-dd", for example "2015-01-01".

#### IPLocationMap
* `getLocalIPLocationMap()`, return the ip-location map.
* `getLocation(String ip)`, get the location of a specific ip address.

#### NetAccount
* `setCallBack(CallBack callback)`, to set async-processor for the async requirement in Android development. `CallBack` is an interface enables process the HTTP response asynchronously if programmed properly.
* `sendLoginRequest()`, to send login request and load login page. The most import response is the `captcha` image(验证码图片). You should store or show the image bytes in `showCaptcha` interface.
* `dologin(String account, String password, String captcha)`, to login before you charge. If login succeeds, the `sendChargeRequest()` will be sent to the server automatically.
* `sendChargeRequest()`, to send recharge request which also involves `captcha` image.
* `postChargeForm(int payamount, String captcha)`, to send recharge POST request.

#### CallBack
This is an interface should be used with `NetAccount` instance.
* `showCaptcha(byte[] captcha, int phase)`, to process captcha in a certain request `phase` (0 means login phase, 1 means recharge phase).
* `showError(String error, int phase)`, to process errors or exception in the a certain `phase`.
* `chargeSucceed(String balance)`, to show the `balance`(余额) after recharge succeeds.

[Jsoup]:http://jsoup.org/
[gateway]:http://gwself.bupt.edu.cn
