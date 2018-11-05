package run_steem_api;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

// Unirest 라이브러리를 활용하여 스팀 API를 실행하는 예를 보여줌
public class UnirestEx {

	/**
	 * condenser_api.get_blog API를 활용하여 최근 포스트 제목들을 리스트로 구함
	 * @param account 대상 스팀잇 계정
	 * @param count 포스트 개수
	 * @return 최근 포스트 제목 리스트
	 */
	public static List<String> getTitleListFromBlog(String account, int count) {
		try {
			HttpResponse<JsonNode> response = Unirest.post("https://api.steemit.com")
				.header("Content-Type", "application/json")
				.header("Cache-Control", "no-cache")
				.body(String.format("{\"jsonrpc\":\"2.0\", \"method\":\"condenser_api.get_blog\", \"params\":[\"%s\", 0, %d], \"id\":1}", account, count))
				.asJson();
			JSONObject jsonResponse = response.getBody().getObject();

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
		catch (UnirestException e) {
			e.printStackTrace();
			return new LinkedList<>();
		}
	}

	public static void main(String[] args) {
		List<String> titleListFromFeed = getTitleListFromBlog("dorian-lee", 10);

		for (String title : titleListFromFeed) {
			System.out.println(title);
		}
	}
}
