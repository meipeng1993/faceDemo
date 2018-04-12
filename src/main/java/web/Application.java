package web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.net.URLEncoder;

@SpringBootApplication
public class Application {

	private static String clientId = "z0Fc9cz1UVGD2BQNvzZmakwR";
	private static String clientSecret = "tXzRk8xEsk2HiRCTduNggupqKEGjOr8X";
	private static String access_token = "";

	public static void main(String[] args) {
		access_token = getAuth(clientId, clientSecret);
		match("F:\\main.jpg", "F:\\minorThree.jpg", "F:\\minor.jpg");
	}

	public static String getAuth(String ak, String sk) {
		// 获取token地址
		String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
		String getAccessTokenUrl = authHost
				// 1. grant_type为固定参数
				+ "grant_type=client_credentials"
				// 2. 官网获取的 API Key
				+ "&client_id=" + ak
				// 3. 官网获取的 Secret Key
				+ "&client_secret=" + sk;
		try {
			URL realUrl = new URL(getAccessTokenUrl);
			// 打开和URL之间的连接
			HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.err.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String result = "";
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			/**
			 * 返回结果示例
			 */
			System.err.println("result:" + result);
			JSONObject jsonObject = new JSONObject(result);
			String access_token = jsonObject.getString("access_token");
			return access_token;
		} catch (Exception e) {
			System.err.printf("获取token失败！");
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static String match(String... path) {
		// 请求url
		String url = "https://aip.baidubce.com/rest/2.0/face/v2/match";
		try {
			String param = "images=";
			// 本地文件路径
			for (int i = 0; i < path.length; i++) {
				byte[] imgData = FileUtil.readFileByBytes(path[i]);
				String imgStr = Base64Util.encode(imgData);
				String imgParam = URLEncoder.encode(imgStr, "UTF-8");
				if (i == 0) {
					param = param + imgParam;
				} else {
					param = param + "," + imgParam;
				}
			}
			// 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
			// String accessToken = "[调用鉴权接口获取的token]";

			String result = HttpUtil.post(url, access_token, param);
			System.out.println(result);
			// 返回示例{"result":[{"index_i":"0","index_j":"1","score":91.580947875977},{"index_i":"0","index_j":"2","score":100},
			// {"index_i":"1","index_j":"2","score":91.580947875977}],"result_num":3,"log_id":3216101491041222}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
