package com.example.library_master;

public class Message {
	
	private String BooklistISBN;
	private String UserId;
	private String ReservationGiveTime;
	private String BookId;
	
	public Message(String BooklistISBN, String UserId, String ReservationGiveTime, String BookId)
	{
		this.BooklistISBN = BooklistISBN;
		this.UserId = UserId;
		this.ReservationGiveTime = ReservationGiveTime;
		this.BookId = BookId;
	}
	public String getBooklistISBN() {
		return BooklistISBN;
	}
	public String getUserId() {
		return UserId;
	}
	public String getReservationGiveTime() {
		return ReservationGiveTime;
	}
	public String getBookId() {
		return BookId;
	}

}