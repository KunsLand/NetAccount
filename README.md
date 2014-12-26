This project is a pure java implementation with Jsoup API & HttpURLConnection API for BUPT wifi gateway login and management. It is __NOT__ official.

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
