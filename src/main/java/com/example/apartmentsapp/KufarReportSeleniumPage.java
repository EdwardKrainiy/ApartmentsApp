package com.example.apartmentsapp;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class KufarReportSeleniumPage {
  @FindBy(xpath = "//*[@id=\"description\"]/div")
  public WebElement kufarAdDescription;

  @FindBy(xpath = "//*[@id=\"sidebar-buttons\"]/button[2]")
  public WebElement kufarRingButton;

  @FindBy(xpath = "//*[@id=\"sidebar-buttons\"]/div[2]/div/div/a")
  public WebElement kufarPhoneNumber;

  @FindBy(xpath = "//*[@id=\"sidebar-buttons\"]/div[2]/span/img")
  public WebElement kufarCloseRingMenu;

  @FindBy(xpath = "//*[@id=\"__next\"]/div[3]/div/div[2]/button")
  public WebElement closeCookiesButtton;

  public WebDriver driver;

  public String getKufarAdDesc(){
    return kufarAdDescription.getText();
  }

  public void clickCloseCookiesButton(){
    closeCookiesButtton.click();
  }

  public void clickKufarRingButton(){
    kufarRingButton.click();
  }

  public String getKufarPhoneNumber() throws InterruptedException {
    clickCloseCookiesButton();
    clickKufarRingButton();
    wait(2000);
    String phone = driver.findElement(By.xpath("//*[@id=\"sidebar-buttons\"]/div[2]/div/div/a")).getText();
    clickCloseRingButton();
    return phone;
  }

  public void clickCloseRingButton(){
    kufarCloseRingMenu.click();
  }

  public KufarReportSeleniumPage(WebDriver driver){
    PageFactory.initElements(driver, this);
    this.driver = driver;
  }
}
