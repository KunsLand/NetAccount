package cn.edu.bupt;

public interface CallBack {
	public void doResponseMessage(MessageType type);
	
	public enum MessageType{
		CAPTCHA_LOAD_ERROR,
		LOGIN_FAILED,
		LOGIN_SUCCESS,
		PAY_FAILED,
		PAY_SUCCESS,
		PAGE_LOAD_ERROR,
		PAYAMOUNT_INVALID
	}
}
