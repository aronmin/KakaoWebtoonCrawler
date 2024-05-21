import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import db.DBBaseInsert;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KakaoWebtoonCrawler {
    private final WebDriver driver;
    private final List<String> titles;
    private final List<String> genres;
    private final List<String> directors;
    private final List<String> descriptions;
    private final List<String> imgs;
    private final List<String> urls;

    public KakaoWebtoonCrawler(WebDriver driver) {
        this.urls = new ArrayList<>();
        this.driver = driver;
        this.titles = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.directors = new ArrayList<>();
        this.descriptions = new ArrayList<>();
        this.imgs = new ArrayList<>();
    }

    public List<String> getTitles() {
        return titles;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public List<String> getUrls() {
        return urls;
    }

    private DBBaseInsert dbInsert = new DBBaseInsert();

    public void crawl(String startUrl) throws InterruptedException, SQLException {
        driver.get(startUrl);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long startTime = System.currentTimeMillis();
        long timeout = 20000;
        while (System.currentTimeMillis() - startTime < timeout) {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(2000);
        }

        List<WebElement> aTags1 = driver.findElements(By.cssSelector("div.w-full.overflow-hidden div[style*='border-color:transparent;border-width:4px'] a"));
        List<WebElement> aTags2 = driver.findElements(By.cssSelector("div.w-full.overflow-hidden div[style*='border-color: transparent; border-width: 4px;'] a"));
        List<WebElement> allATags = new ArrayList<>();
        allATags.addAll(aTags1);
        allATags.addAll(aTags2);
        for (WebElement aTag : allATags) {
            String href = aTag.getAttribute("href");
            urls.add(href);
        }

        int cnt = 1;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("KakaoWebtoon.txt"));) {
            for (String url : urls) {
                try {
                    driver.get(url);
                    Thread.sleep(2000);

                    WebElement title = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div/div[2]/a/div/span[1]"));
                    titles.add(title.getText());
                    writer.write(cnt + "번째 웹툰" + "\n");
                    writer.write("Title : " + title.getText() + "\n");
                    System.out.println(cnt + "번째 웹툰");
                    System.out.println("Title : " + title.getText());

                    WebElement director = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div/div[2]/a/div/span[2]"));
                    directors.add(director.getText());
                    writer.write("Director : " + director.getText() + "\n");
                    System.out.println("Director : " + director.getText());

                    WebElement genre = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div/div[2]/a/div/div[1]/div[1]/div/span[2]"));
                    String genreText = genre.getText();
                    genres.add(genreText);
                    writer.write("Genre : " + genreText + "\n");
                    System.out.println("Genre : " + genreText);

                    WebElement img = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div/div[1]/div[2]/div/div/div[2]/img"));
                    imgs.add(img.getAttribute("src"));
                    writer.write("Img : " + img.getAttribute("src") + "\n");
                    System.out.println("Img : " + img.getAttribute("src"));

                    String more_info = url + "?tab_type=about";
                    driver.get(more_info);
                    Thread.sleep(2000);
                    WebElement description = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[2]/div[2]/div/div/div[1]/div/div[2]/div/div/span"));
                    descriptions.add(description.getText());
                    writer.write("Description : " + description.getText().replace("\n", "") + "\n");
                    System.out.println("Description : " + description.getText().replace("\n", ""));
                    writer.write("Url : " + url + "\n");
                    System.out.println("Url : " + url);
                    writer.write("----------------------------------------------------" + "\n");
                    System.out.println("----------------------------------------------------");
                    cnt++;

                } catch (Exception e) {
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int index = 0; index < titles.size(); index++) {
            try {
                dbInsert.contentInsert(titles.get(index), imgs.get(index), descriptions.get(index).replace("\n", "") + "\n", directors.get(index), null, urls.get(index), "kakaowebtoon");
                dbInsert.genreInsert(dbInsert.searchMovieID("kakaowebtoon", titles.get(index)), dbInsert.searchGenreID(genres.get(index)), "kakaowebtoon_genre");
            } catch (SQLException e) {
                System.err.println("DB 삽입 중 오류가 발생했습니다: " + e.getMessage());
            }
        }
    }
}
