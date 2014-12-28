package cn.edu.bupt;

public interface CallBack {
	public void showCaptcha(byte[] captcha, int phase);
	public void showError(String error, int phase);
	public void chargeSucceed(String balance);
}
