package cn.edu.bupt;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Scanner;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

public class NetAccount {
	private Map<String, String> cookies = null;
	private CallBack callback = null;
	
	public NetAccount(CallBack callback){
		this.callback = callback;
	}
	
	public void loadLoginPage(){
		String url = "http://netaccount.bupt.edu.cn/login";
		Response res = null;
		try {
			res = Jsoup.connect(url).execute();
			cookies = res.cookies();
		} catch (IOException e) {
			if(callback != null)
				callback.doResponseMessage(
					CallBack.MessageType.PAGE_LOAD_ERROR);
		}
	}
	
	public byte[] getCaptcha(){
		String url = "http://netaccount.bupt.edu.cn/authcode";
		Response res = null;
		byte[] captcha = null;
		try {
			res = Jsoup.connect(url).cookies(cookies)
					.ignoreContentType(true).execute();
			captcha = res.bodyAsBytes();
			cookies = res.cookies();
		} catch (IOException e) {
			if(callback != null)
				callback.doResponseMessage(
					CallBack.MessageType.CAPTCHA_LOAD_ERROR);
		}
		return captcha;
	}
	
	public void doLogin(String account, String password, String captcha){
		String url = "http://netaccount.bupt.edu.cn/dologin";
		Response res = null;
		try {
			res = Jsoup.connect(url).cookies(cookies)
					.data("user", account)
					.data("pass", password)
					.data("captcha", captcha)
					.method(Method.POST).execute();
			cookies = res.cookies();
			if(res.parse().select("alert-error").isEmpty())
				callback.doResponseMessage(CallBack.MessageType.LOGIN_SUCCESS);
			else 
				callback.doResponseMessage(CallBack.MessageType.LOGIN_FAILED);
		} catch (IOException e) {
			if(callback != null)
				callback.doResponseMessage(
					CallBack.MessageType.LOGIN_FAILED);
		}
	}
	
	public void loadPayPage(){
		String url = "http://netaccount.bupt.edu.cn/Info/pay";
		Response res;
		try {
			res = Jsoup.connect(url).cookies(cookies).execute();
			cookies = res.cookies();
		} catch (IOException e) {
			if(callback != null)
				callback.doResponseMessage(
					CallBack.MessageType.PAGE_LOAD_ERROR);
		}
	}
	
	public void postPayForm(int payamount, String captcha){
		if(payamount < 5 || payamount > 100) {
			if(callback != null)
				callback.doResponseMessage(
						CallBack.MessageType.PAYAMOUNT_INVALID);
			return;
		}
		String url = "http://netaccount.bupt.edu.cn/Info/pay";
		Response res;
		try {
			res = Jsoup.connect(url).cookies(cookies)
					.data("payamount", "" + payamount)
					.data("captcha", captcha)
					.method(Method.POST).execute();
			cookies = res.cookies();
			if(res.parse().select("alert-error").isEmpty())
				callback.doResponseMessage(CallBack.MessageType.PAY_SUCCESS);
			else 
				callback.doResponseMessage(CallBack.MessageType.PAY_FAILED);
		} catch (IOException e) {
			if(callback != null)
				callback.doResponseMessage(
					CallBack.MessageType.PAY_FAILED);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NetAccount netAccount = new NetAccount(null);
		try {
			String path = "/home/likun/Downloads/captcha.png";
			
			netAccount.loadLoginPage();
			OutputStream out =
					new BufferedOutputStream(new FileOutputStream(path));
		    out.write(netAccount.getCaptcha());
		    out.close();
		    
		    String account = "xxx", password = "yyy";
		    String captcha = null;
		    @SuppressWarnings("resource")
			Scanner scan = new Scanner(System.in);
		    captcha = scan.nextLine();
		    netAccount.doLogin(account, password, captcha);
		    
		    netAccount.loadPayPage();
		    out = new BufferedOutputStream(new FileOutputStream(path));
		    out.write(netAccount.getCaptcha());
		    out.close();
		    
		    int payamount = 10;
		    captcha = scan.nextLine();
		    netAccount.postPayForm(payamount, captcha);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
