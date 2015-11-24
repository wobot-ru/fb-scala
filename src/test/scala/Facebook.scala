import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.scalatest.FlatSpec
import org.scalatest.selenium.WebBrowser

import scala.collection.mutable

class Facebook extends FlatSpec with WebBrowser {
  System.setProperty("webdriver.chrome.driver", "/bin/chromedriver/chromedriver.exe");
  val opt = new ChromeOptions()
  opt.addArguments("--user-data-dir=/tmp/fb/chrome");
  implicit val webDriver: WebDriver = new ChromeDriver(opt);

  def convertToUserFriend(url: String): String = {
    url match {
      case u if u.contains("profile.php") => url.substring(0, url.indexOf('&')) + "&sk=friends"
      case _ => url.substring(0, url.indexOf('?')) + "?sk=friends"
    }
  }

  "Facebook" should "be crawled" in {
    var iterationCount = 10;
    var profiles = mutable.Stack[String]()
    profiles.push("https://www.facebook.com/zuck?sk=friends");

    while ((!profiles.isEmpty) && iterationCount > 0) {
      iterationCount = iterationCount - 1;
      val profile: String = profiles.pop()
      println(profile)
      go to profile
      val friends: Iterator[Element] = findAll(cssSelector(".fsl.fwb.fcb a"))
      for (f <- friends) {
        f.attribute("href") match {
          case Some(url) =>
            val profileFriendsPage: String = convertToUserFriend(url)
            println("            " + profileFriendsPage)
            profiles.push(profileFriendsPage)
        }
      }
    }
  }
}