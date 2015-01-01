package cn.edu.bupt;

import java.util.List;

public interface CallBack {
	public void showIpList(List<String> ipList);
	public void showCaptcha(byte[] captcha, Action action);
	public void showError(String error, Action action);
	public void showForceOfflineResult(String msg);
	public void showQueryResult(LoginLog log);
	public void chargeSucceed(String balance);
	
	public enum Action{
		LOGIN,
		FORCE_OFFLINE,
		QUERY,
		RECHARGE,
	}
}
