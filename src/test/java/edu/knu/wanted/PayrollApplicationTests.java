package edu.knu.wanted;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SpringBootTest
class PayrollApplicationTests {
	UserController userController = new UserController();

	@Test
	public void TestUserRating() {
		String uid = null;
		assertFalse(userController.userRating(uid).isEmpty());

		uid = "inu";
		assertFalse(userController.userRating(uid).isEmpty());
	}

	@Test
	public void TestUserCount() {
		long count = userController.userCount();
		assert count>=0: "fail to user count";
	}

	@Test
	public void TestNewUser() {
		HashMap<String, String> status= new HashMap<String, String>();

		status.put("result", "SUCCESS");
		assertEquals(status, userController.newUser2("user", "1123"));

		status.put("result", "SUCCESS");
		assertEquals(status, userController.newUser2("hiapple", "1123"));

		status.put("result", "FAILED");
		assertEquals(status, userController.newUser2("inu", "1123"));
	}

	@Test
	public void TestDeleteUser() {
		HashMap<String, String> status= new HashMap<String, String>();

		status.put("result", "SUCCESS");
		assertEquals(status, userController.deleteUser("hiapple"));

		status.put("result", "FAILED");
		assertEquals(status, userController.deleteUser("hiapple"));
	}

	@Test
	public void TestDelete2User() {
		HashMap<String, String> status= new HashMap<String, String>();

		status.put("result", "SUCCESS");
		assertEquals(status, userController.deleteUser2("user"));

		status.put("result", "FAILED");
		assertEquals(status, userController.deleteUser2("user"));
	}

	@Test
	public void TestFindRating(){
		String uid = "hiapple", movie = "1", rating = "3";
		HashMap<String, String> status = new HashMap<>();

		status.put("result", "SUCCESS");
		assertEquals(userController.findRating("inu", "1", "5"), status);

		status.put("result", "FAILED");
		assertEquals(userController.findRating(uid, movie, rating), status);
	}

	@Test
	public void TestRatingList() {
		List<HashMap<String, String>> list2 = new ArrayList<>();
		HashMap<String, String> status = new HashMap<>();

		assertFalse(userController.list_ratings2("inu").isEmpty());

		status.put("result", "Cannot find user");
		list2.add(status);
		assertEquals(userController.list_ratings2("hiapple"), list2);
	}

}
