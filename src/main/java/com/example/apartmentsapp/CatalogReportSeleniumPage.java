package com.example.apartmentsapp;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CatalogReportSeleniumPage {
  @FindBy(
      xpath = "//*[@id=\"container\"]/div/div[2]/div/div/div[2]/div[5]/div[3]/div/div[1]/div[1]")
  public WebElement catalogAdDescription;

  @FindBy(xpath = "//*[@id=\"apartment-phones\"]/ul/li[2]/a")
  public WebElement catalogPhone;

  public WebDriver driver;

  public String getCatalogAdDesc(){
    return catalogAdDescription.getText();
  }

  public String getCatalogPhone(){
    return catalogPhone.getText();
  }

  public CatalogReportSeleniumPage(WebDriver driver){
    PageFactory.initElements(driver, this);
    this.driver = driver;
  }
}
