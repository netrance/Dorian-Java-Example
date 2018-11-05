package run_steem_api;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// OkHttp 라이브러리를 활용하여 스팀 API를 실행하는 예를 보여줌
public class OkHttpEx {

	/**
	 * condenser_api.get_feed API를 활용하여 피드의 최근 포스트 제목들을 리스트로 구함
	 * @param account 대상 스팀잇 계정
	 * @param count 포스트 개수
	 * @return 최근 포스트 제목 리스트
	 */
	public static List<String> getTitleListFromFeed(String account, int count) {
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(
			mediaType,
			String.format("{\"jsonrpc\":\"2.0\", \"method\":\"condenser_api.get_feed\", \"params\":[\"%s\", 0, %d], \"id\":1}", account, count)
		);
		Request request = new Request.Builder()
			.url("https://api.steemit.com")
			.post(body)
			.addHeader("Content-Type", "application/json")
			.addHeader("Cache-Control", "no-cache")
			.build();

		try {
			// 스팀 서버로부터 피드를 받음
			Response response = client.newCall(request).execute();
			String strResponse = response.body().string();
			JSONObject jsonResponse = new JSONObject(strResponse);

			// 받은 피드로부터 타이틀 리스트 생성
			List<String> titleList = new LinkedList<>();
			JSONArray jsonResult = jsonResponse.getJSONArray("result");
			for (int i = 0; i < jsonResult.length(); i++) {
				JSONObject jsonPost = jsonResult.getJSONObject(i);
				JSONObject jsonComment = jsonPost.getJSONObject("comment");
				int id = jsonComment.getInt("id");

				// id가 0인 빈 포스트는 건너뜀
				if (id != 0) {
					titleList.add(jsonComment.getString("title"));
				}
			}

			return titleList;
		}
		catch (IOException e) {
			e.printStackTrace();
			return new LinkedList<>();
		}
	}

	public static void main(String[] args) {
		List<String> titleListFromFeed = getTitleListFromFeed("dorian-lee", 10);

		for (String title : titleListFromFeed) {
			System.out.println(title);
		}
	}
}
