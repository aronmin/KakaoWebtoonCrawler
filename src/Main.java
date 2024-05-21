import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws InterruptedException, SQLException {
        System.setProperty("webdriver.chrome.driver", "util/driver/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless");
        options.addArguments("no-sandbox");
        options.addArguments("disable-gpu");
        options.addArguments("--lang=ko_KR");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

        WebDriver driver = new ChromeDriver();
        KakaoWebtoonCrawler crawler = new KakaoWebtoonCrawler(driver);
        try {
            crawler.crawl("https://page.kakao.com/menu/10010/screen/93");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.quit();
    }
}
