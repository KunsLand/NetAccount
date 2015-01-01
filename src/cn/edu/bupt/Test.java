package cn.edu.bupt;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Scanner;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    @SuppressWarnings("resource")
		final Scanner scan = new Scanner(System.in);
	    System.out.println("Account:");
	    String account = scan.nextLine();
	    System.out.println("Password:");
	    String password = scan.nextLine();
		final NetAccount na = new NetAccount(account, password);
	    System.out.println("Set captcha storage path:");
		final String path = scan.nextLine();
		CallBack callback = new CallBack(){
			@Override
			public void showCaptcha(byte[] captcha, Action action) {
				OutputStream out = null;
				try {
					out = new BufferedOutputStream(
							new FileOutputStream(path + "/captcha.png"));
					out.write(captcha);
					out.close();
				    System.out.println("Captcha:");
				    String captchaStr = scan.nextLine();
				    if(action == Action.LOGIN)
				    	na.doLogin(captchaStr);
				    else if(action == Action.RECHARGE)
				    	na.postChargeForm(10, captchaStr);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void showError(String error, Action action) {
				System.out.println(action + ": " + error);
			}

			@Override
			public void chargeSucceed(String balance) {
				System.out.println("Recharge Succeed. Now Your balance is: "
						+ balance);
			}

			@Override
			public void showIpList(List<String> ipList) {
				System.out.println("Online IPs: " + ipList);
			    System.out.println("IP(to force offline):");
				String ip = scan.nextLine();
				na.forceOffline(ip);
			}

			@Override
			public void showQueryResult(LoginLog log) {
				System.out.println(log);
			}

			@Override
			public void showForceOfflineResult(String msg) {
				System.out.println(msg);
			    System.out.println("Query month:");
			    String month = scan.nextLine();
				na.sendQueryRequest(month);
			}
			
		};
		na.setCallBack(callback);
		na.sendLoginRequest();
	}

}
