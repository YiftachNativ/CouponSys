package com.javabeans;
import java.sql.*;
import java.time.LocalDate;



public class Coupon {

	// Attributes

	
	
	private long id;
	private String title;
	private LocalDate startDate;
	private LocalDate endDate;
	private int amount;
	private CouponType type ;
	private String message;
	private double price;
	private String image;
	
	
	// Constructor
	public Coupon(){}
	
	public Coupon(String title, LocalDate startDate, LocalDate endDate, int amount, CouponType type, String message, double price, String image){
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amount = amount;
		this.type = type;
		this.message = message;
		this.price = price;
		this.image = image;
	}

	public Coupon(long id, String title, LocalDate stDate, LocalDate enDate, int amount, CouponType type,  String message, double price, String image) {
		this.id = id;
		this.title = title;
		this.startDate = stDate;
		this.amount = amount;
		this.type = type;
		this.endDate = enDate;
		this.message = message;
		this.price = price;
		this.image = image;
		
	}
	
	//Getters && Setters

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public LocalDate getStartDate() {
		return startDate;
	}


	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}


	public LocalDate getEndDate() {
		return endDate;
	}


	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}


	public int getAmount() {
		return amount;
	}


	public void setAmount(int amount) {
		this.amount = amount;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public double getPrice() {
		return price;
	}


	public void setPrice(double price) {
		this.price = price;
	}


	public String getImage() {
		return image;
	}


	public void setImage(String image) {
		this.image = image;
	}
	
	// ToString
	@Override
	public String toString() {
		return "Coupon [id=" + id + ", title=" + title + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", Category= " + type + ", amount=" + amount + ", message=" + message + ", price=" + price + ", image=" + image + "]";
	}

	public CouponType getType() {
		return type;
	}

	public void setType(CouponType type) {
		this.type = type;
	}
}
