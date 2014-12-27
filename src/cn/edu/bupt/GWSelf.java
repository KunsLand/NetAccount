package cn.edu.bupt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.bupt.LoginLog.LogItem;
import cn.edu.bupt.LoginLog.LogType;

import com.google.gson.Gson;

public class GWSelf {
	private Map<String, String> cookies = null;
	private Map<String, String> ipMap = null;
	private GWJsonMessage info = null;
	
	public void login(String account, String password)
			throws IOException, NoSuchAlgorithmException{
		String url = null, body = null, checkcode = null;
		cookies = new HashMap<String, String>();
		Response res = null;
		
		url = "http://gwself.bupt.edu.cn/nav_login";
		res = Jsoup.connect(url).execute();
		body = res.body();
		cookies = res.cookies();
		checkcode = getCheckCode(body);
		
		url = "http://gwself.bupt.edu.cn/RandomCodeAction.action?" +
				"randomNum=" + Math.random();
		Jsoup.connect(url).cookies(cookies)
			.ignoreContentType(true).execute();

		url = "http://gwself.bupt.edu.cn/LoginAction.action";
		res = Jsoup.connect(url).cookies(cookies)
				.method(Method.POST)
				.data("account", account)
				.data("password",getMD5Hex(password))
				.data("code", "")
				.data("checkcode", checkcode)
				.data("Submit", "登 录")
				.execute();
		info = getInformation();
	}
	
	private String getMD5Hex(String str)
			throws UnsupportedEncodingException, NoSuchAlgorithmException{
		byte[] bytes = str.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hash = md.digest(bytes);
		return new BigInteger(1,hash).toString(16);
	}
	
	private String getCheckCode(String html){
		String ans = null, prefix = "checkcode=\"", postfix = "\";";
		ans = html.substring(html.indexOf(prefix) + prefix.length());
		ans = ans.substring(0, ans.indexOf(postfix));
		return ans;
	}
	
	public GWJsonMessage getInformation() throws IOException{
		String url = "http://gwself.bupt.edu.cn/refreshaccount?t="
				+ Math.random();
		info = new Gson().fromJson(
				Jsoup.connect(url).cookies(cookies)
					.ignoreContentType(true).execute().body(),
				GWJsonMessage.class);
		return info;
	}
	
	public List<String> getOnlineIps() throws IOException{
		String url = "http://gwself.bupt.edu.cn/nav_offLine";
		Document doc = Jsoup.connect(url).cookies(cookies).get();
		Elements elements = doc.select("tbody > tr");
		ipMap = new HashMap<String, String>();
		for(Element e: elements){
			String ip = e.child(0).ownText().replaceAll("\u00a0", ""),
					fldsessionid = e.child(3).ownText();
			ipMap.put(ip, fldsessionid);
		}
		return new ArrayList<String>(ipMap.keySet());
	}
	
	public GWJsonMessage forceOffline(String ip) throws IOException{
		String url = "http://gwself.bupt.edu.cn/tooffline?t="
				+ Math.random() + "&fldsessionid=" + ipMap.remove(ip);
		return new Gson().fromJson(
				Jsoup.connect(url).cookies(cookies)
					.ignoreContentType(true).execute().body(),
				GWJsonMessage.class);
	}
	
	public LoginLog getLoginLog(LogType type) throws IOException{
		String url = "http://gwself.bupt.edu.cn/UserLoginLogAction.action";
		Map<String, String> data = new HashMap<String, String>();
		Response res = null;
		data.put("type", type == LogType.DAY ? "1": "2");
		data.put("startDate", info.serverDate.substring(0, 8) + "01");
		data.put("endDate", info.serverDate);
		res = Jsoup.connect(url).cookies(cookies)
				.method(Method.POST).data(data).execute();
		return parseLogDocument(res.parse());
	}
	
	/**
	 * 
	 * @param startDate
	 * for example: 2014-01-01
	 * @param endDate
	 * for example: 2014-12-21
	 * @return
	 * @throws IOException
	 */
	public LoginLog getLoginLog(String startDate, String endDate)
			throws IOException{
		Response res = null;
		String url = "http://gwself.bupt.edu.cn/UserLoginLogAction.action";
		Map<String, String> data = new HashMap<String, String>();
		data.put("type", "4");
		data.put("startDate", startDate);
		data.put("endDate", endDate);
		res = Jsoup.connect(url).cookies(cookies)
				.method(Method.POST).data(data).execute();
		return parseLogDocument(res.parse());
	}
	
	private LoginLog parseLogDocument(Document doc){
		LoginLog log = new LoginLog();
		Elements data = null;
		data = doc.select("table.table2 font");
		log.upload = Double.valueOf(data.get(0).ownText());
		log.download = Double.valueOf(data.get(1).ownText());
		log.total = Double.valueOf(data.get(2).ownText());
		log.fees = Double.valueOf(data.get(3).ownText());
		log.minutes = Integer.valueOf(data.get(4).ownText());
		data = doc.select("table#example > tbody > tr");
		log.logs = new ArrayList<LogItem>();
		for (Element e: data){
			LogItem item = log.new LogItem();
			item.loginTime = e.child(0).ownText();
			item.logoutTime = e.child(1).ownText();
			item.minutes = Integer.valueOf(e.child(2).ownText().trim());
			item.total = Double.valueOf(e.child(3).ownText().trim());
			item.fees = Double.valueOf(e.child(4).ownText().trim());
			item.upload = Double.valueOf(e.child(5).ownText().trim());
			item.download = Double.valueOf(e.child(6).ownText().trim());
			item.ip = e.child(7).ownText();
			log.logs.add(item);
		}
		return log;
	}
	
	public static void main(String[] args) {
		try {
			GWSelf gw = new GWSelf();
			gw.login("xxx", "yyy");
			System.out.println(gw.getInformation());
			List<String> ips = gw.getOnlineIps();
			System.out.println(ips);
//			System.out.println(gw.forceOffline(ips.get(0)));
//			System.out.println(gw.getLoginLog(LogType.DAY));
//			System.out.println(gw.getLoginLog(LogType.MONTH));
			LoginLog log = gw.getLoginLog("2014-01-01", "2014-12-21");
			for(LogItem item: log.logs)
				System.out.println(IPLocationMap.getLocation(item.ip));
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

}
