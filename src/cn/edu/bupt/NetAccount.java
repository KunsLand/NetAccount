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
	
	public void setCallBack(CallBack callback){
		this.callback = callback;
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
			callback.showCaptcha(res.bodyAsBytes(), 0);
		} catch (IOException e) {
			callback.showError("Failed in sending LOGIN request.", 0);
		}
	}

	public void doLogin(String account, String password, String captcha) {
		try {
			String url = "http://netaccount.bupt.edu.cn/dologin";
			Response res = Jsoup.connect(url).cookies(cookies)
					.data("user", account).data("pass", password)
					.data("captcha", captcha).method(Method.POST).execute();
			cookies = res.cookies();
			if(res.parse().select("div.alert-error").isEmpty()){
				sendChargeRequest();
			} else callback.showError("Login failed.", 0);
		} catch (IOException e) {
			callback.showError("Login failed.", 0);
		}
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
			callback.showCaptcha(res.bodyAsBytes(), 1);
		} catch (IOException e) {
			callback.showError("Failed in sending CHARGE request.", 1);
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
			} else callback.showError("Charge failed", 1);
		} catch (IOException e) {
			callback.showError("Charge failed.", 1);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final NetAccount na = new NetAccount();
		CallBack callback = new CallBack(){
			@Override
			public void showCaptcha(byte[] captcha, int phase) {
				String path = "/home/likun/Downloads/captcha.png";
				OutputStream out = null;
				try {
					out = new BufferedOutputStream(new FileOutputStream(path));
					out.write(captcha);
					out.close();
				    @SuppressWarnings("resource")
					Scanner scan = new Scanner(System.in);
				    String captchaStr = scan.nextLine();
				    if(phase == 0)
				    	na.doLogin("xxx", "yyy", captchaStr);
				    else
				    	na.postChargeForm(10, captchaStr);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void showError(String error, int phase) {
				System.out.println(phase + ": " + error);
			}

			@Override
			public void chargeSucceed(String balance) {
				System.out.println("Charge Succeed. Now Your balance is: "
						+ balance);
			}
			
		};
		na.setCallBack(callback);
		na.sendLoginRequest();
	}
}
