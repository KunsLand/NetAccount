This project is a pure java implementation with Jsoup & HttpURLConnection for BUPT wifi gateway login and management. The former `BUPTWifiHelper` project failed in logining with the Jsoup & HttpURLConnection API because that I did not figure out the gateway needs two requests before posting login form, `http://gwself.bupt.edu.cn/nav_login` & `http://gwself.bupt.edu.cn/RandomCodeAction.action`. The second GET request returns a random code image and MUST be done with a session cookie which can be obtained in the first request response header. I ignored the second request before because the random code image is not used in the later requests. It seems that the second request is used to inform the server side instead of filling the form. The `checkcode` in the login post form can be retrieved in the javascript source code in the login html.

# API

#### GWSelf
* `login(String account, String password)`, to login wifi gateway.
* `getInformation()`, get account information including: `leftmoeny`, `onlinestate`, `status`, `welcome`, etc.
* `getOnlineIps()`, get all online IPs.
* `forceOffline(String ip)`, force offline the specified ip.
* `getLoginLog(LogType type)`, get the user login and logout logs. The logs contains `loginTime`, `logoutTime`, `minutes`, `fees`, `upload`, `download`, `ip`, etc. There are two types of logs now, `DAY` (means current date) and `Month` (means current month).
* `getLoginLog(String startDate, String endDate)`, get user login and logout logs during a specified time.

#### IPLocationMap
* `getLocalIPLocationMap()`, return the ip-location map.
* `getLocation(String ip)`, get the location of a specific ip address.