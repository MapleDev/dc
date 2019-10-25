package me.crycry.zuul.service;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 * 验证服务�?
 * @author zhuweizhong
 *
 */
@Component
public class VerifyService {

	//private String[] defaultImgConfig = new String[]{"no", "105,179,90", "150", "45", "51,161,219", "30", "微软雅黑", "3", "4"};
//	private String[] defaultImgConfig = new String[]{"no", "0,0,0", "150", "50", "0,204,102", "35", "微软雅黑", "10", "4"};

	/**
	 * 生成验证码并返回图片�?
	 * @param request
	 * @return
	 */
	public BufferedImage createVcode(HttpServletRequest request) {

		Producer producer = getVcodeProducer();
		String capText = producer.createText();
		BufferedImage bi = producer.createImage(capText);
		HttpSession session = request.getSession();
		session.setAttribute("vcode", capText);
		return bi;
	}

	/**
	 * 获取验证码生成对�?
	 * @return
	 */
	public Producer getVcodeProducer() {

		DefaultKaptcha captchaProducer = new DefaultKaptcha();

		Properties properties = new Properties();
		properties.setProperty(Constants.KAPTCHA_BORDER, "no"); // 边框
		properties.setProperty(Constants.KAPTCHA_BORDER_COLOR, "255,255,255"); // 边框颜色

		properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, "150");  // 图片�?
		properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, "50"); // 图片�?

//		properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy"); // 样式:阴影
		properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.WaterRipple"); // 样式:水纹

		properties.setProperty(Constants.KAPTCHA_BACKGROUND_IMPL, "com.google.code.kaptcha.impl.DefaultBackground"); // 背景
		properties.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_FROM, "255,255,255"); // 背景颜色渐变，开始颜�?
		properties.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_FROM, "255,255,255"); // 背景颜色渐变，结束颜�?

		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_IMPL, "com.google.code.kaptcha.text.impl.DefaultTextCreator"); // 文本
//		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, "0,204,102"); // 字体颜色
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, "0,0,0"); // 字体颜色
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, "35"); // 字体大小
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES, "微软雅黑"); // 字体
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "5"); // 文字间隔
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4"); // 验证码长�?

//		properties.setProperty(Constants.KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise"); // 干扰
		properties.setProperty(Constants.KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.DefaultNoise"); // 干扰
//		properties.setProperty(Constants.KAPTCHA_NOISE_COLOR, "0,204,102"); // 干扰颜色
		properties.setProperty(Constants.KAPTCHA_NOISE_COLOR, "0,0,0"); // 干扰颜色

		Config config = new Config(properties);
		captchaProducer.setConfig(config);

		return captchaProducer;
	}

	/**
	 * �?��前后端验证码在会话期是否匹配
	 * @param request
	 * @param vcode
	 * @return
	 */
	public boolean checkVcode(HttpServletRequest request, String vcode) {
		HttpSession session = request.getSession();
		System.out.println(session.getId());
		String capText = (String) session.getAttribute("vcode");
		if (capText == null || vcode.length()==0|| !capText.equalsIgnoreCase(vcode)) {
			return false;
		}
		return true;
	}

	/**
	 * 清除会话期间的验证码
	 * @param request
	 */
	public void removeVcode(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
	}

}
