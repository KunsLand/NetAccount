package cn.edu.bupt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.bupt.CallBack.Action;
import cn.edu.bupt.LoginLog.LogItem;

public class NetAccount {
	private Map<String, String> cookies = null;
	private CallBack callback = null;
	private String account = null, password = null;
	private int timeout = 10;
	private boolean autoRefresh = true;
	
	public NetAccount(String account, String password){
		this.account = account;
		this.password = password;
	}
	
	public void setCallBack(CallBack callback){
		this.callback = callback;
	}
	
	public void setTimeout(int seconds){
		this.timeout = seconds;
	}
	
	public void setAutoRefreshIndexPage(boolean auto){
		this.autoRefresh = auto;
	}

	public void sendLoginRequest() {
		try {
			String url = "http://netaccount.bupt.edu.cn/login";
			Response res = Jsoup.connect(url).execute();
			cookies = res.cookies();

			url = "http://netaccount.bupt.edu.cn/authcode";
			res = Jsoup.connect(url).cookies(cookies).ignoreContentType(true)
					.execute();
			cookies = res.cookies();
			callback.showCaptcha(res.bodyAsBytes(), Action.LOGIN);
		} catch (IOException e) {
			callback.showError("Failed in sending LOGIN request: " +
					e.getMessage(),	Action.LOGIN);
		}
	}

	public void doLogin(String captcha) {
		try {
			String url = "http://netaccount.bupt.edu.cn/dologin";
			Response res = Jsoup.connect(url).cookies(cookies)
					.data("user", account).data("pass", password)
					.data("captcha", captcha).method(Method.POST).execute();
			cookies = res.cookies();
			Document doc = res.parse();
			if(doc.select("div.alert-error").isEmpty()){
				callback.showIpList(getIpList(doc));
			} else callback.showError("Login failed.", Action.LOGIN);
		} catch (IOException e) {
			callback.showError("Login failed: " + e.getMessage(),
					Action.LOGIN);
		}
	}
	
	private List<String> getIpList(Document doc){
		Elements el = doc.select("a.remove");
		List<String> ipList = new ArrayList<String>();
		for (Element e: el){
			ipList.add(e.attr("data-ip"));
		}
		return ipList;
	}
	
	public void refreshIndexPage(){
		try {
			String url = "http://netaccount.bupt.edu.cn/";
			Response res = Jsoup.connect(url).cookies(cookies).execute();
			cookies = res.cookies();
			callback.showIpList(getIpList(res.parse()));
		} catch (IOException e) {
			callback.showError("REFRESH INDEX PAGE failed: " + e.getMessage(),
					Action.REFRESH_INDEX_PAGE);
		}
	}
	
	public void forceOffline(String ip){
		try {
			String url = "http://netaccount.bupt.edu.cn/Info/kickself";
			Response res = Jsoup.connect(url).cookies(cookies)
					.timeout(timeout*1000)
					.data("ip", ip).method(Method.POST)
					.ignoreContentType(true).execute();
			cookies = res.cookies();
			callback.showForceOfflineResult(res.body());
		} catch (IOException e) {
			callback.showError("Force offline failed: " + e.getMessage(),
					Action.FORCE_OFFLINE);
		}
		if(autoRefresh) refreshIndexPage();
	}
	
	public void sendQueryRequest(String month){
		try{
			String url = "http://netaccount.bupt.edu.cn/Info/bill" +
					"?date=" + month;
			Response res = Jsoup.connect(url).cookies(cookies).execute();
			cookies = res.cookies();
			callback.showQueryResult(getLoginLog(res.parse()));
		}catch(IOException e){
			callback.showError("Failed in sending RECHARGE request: " +
					e.getMessage(), Action.RECHARGE);
		}
	}
	
	private LoginLog getLoginLog(Document doc){
		Elements tables = doc.select("table");
		if(tables==null || tables.isEmpty()) return null;
		LoginLog log = new LoginLog();
		Elements data = tables.get(0).select("tbody > tr > td");
		log.upload = Double.valueOf(data.get(1).ownText());
		log.download = Double.valueOf(data.get(3).ownText());
		log.total = Double.valueOf(data.get(5).ownText());
		log.fees = Double.valueOf(data.get(7).ownText());
		log.minutes = Integer.valueOf(data.get(9).ownText());
		Elements rows = tables.get(1).select("tbody > tr");
		log.logs = new ArrayList<LogItem>();
		for(Element row: rows){
			LogItem item = log.new LogItem();
			item.loginTime = row.child(0).ownText();
			item.logoutTime = row.child(1).ownText();
			item.minutes = Integer.valueOf(row.child(2).ownText());
			item.total = Double.valueOf(row.child(3).ownText());
			item.fees = Double.valueOf(row.child(4).ownText());
			item.upload = Double.valueOf(row.child(5).ownText());
			item.download = Double.valueOf(row.child(6).ownText());
			item.ip = row.child(7).ownText();
			log.logs.add(item);
		}
		return log;
	}

	public void sendChargeRequest() {
		try {
			String url = "http://netaccount.bupt.edu.cn/Info/pay";
			Response res = Jsoup.connect(url).cookies(cookies).execute();
			cookies = res.cookies();

			url = "http://netaccount.bupt.edu.cn/authcode";
			res = Jsoup.connect(url).cookies(cookies).ignoreContentType(true)
					.execute();
			cookies = res.cookies();
			callback.showCaptcha(res.bodyAsBytes(), Action.RECHARGE);
		} catch (IOException e) {
			callback.showError("Failed in sending RECHARGE request: " +
					e.getMessage(), Action.RECHARGE);
		}
	}

	public void postChargeForm(int payamount, String captcha) {
		try {
			String url = "http://netaccount.bupt.edu.cn/Info/pay";
			Response res = Jsoup.connect(url).cookies(cookies)
					.data("payamount", "" + payamount).data("captcha", captcha)
					.method(Method.POST).execute();
			cookies = res.cookies();
			if(res.parse().select(".alert-error").isEmpty()){
				callback.chargeSucceed(res.parse().select("form > div").get(1)
						.select("div > div").first().text());
			} else callback.showError("Recharge failed", Action.RECHARGE);
		} catch (IOException e) {
			callback.showError("Recharge failed: " + e.getMessage(),
					Action.RECHARGE);
		}
	}
}
