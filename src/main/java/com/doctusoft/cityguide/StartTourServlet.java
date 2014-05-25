package com.doctusoft.cityguide;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doctusoft.cityguide.entity.Card;
import com.doctusoft.cityguide.entity.Place;
import com.doctusoft.cityguide.entity.Tour;
import com.doctusoft.cityguide.entity.User;
import com.doctusoft.cityguide.service.BaseService;
import com.doctusoft.cityguide.service.TimeLineService;
import com.google.common.base.Preconditions;

public class StartTourServlet extends HttpServlet {
	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		
		String tourId = MainServlet.TOUR_ID;
		Preconditions.checkNotNull(tourId);
		
		Tour tour = BaseService.loadEntityById(Tour.class, tourId);
		Preconditions.checkNotNull(tour);
		
		
		String userId = MainServlet.USER_ID;
		User user = BaseService.loadEntityById(User.class, userId);
		
		Preconditions.checkNotNull(user);
		
		user.setActualTourId(tourId);
		
		Card startCard = BaseService.loadEntityById(Card.class, tour.getStartCardId());
		Preconditions.checkNotNull(startCard);
		
		Place firstPlace = BaseService.loadEntityById(Place.class, tour.getPlaceIds().get(0));
		
		
		new TimeLineService().sendInfoCardItem(userId, startCard, firstPlace);
		
		resp.setContentType("text/html");
		Writer writer = resp.getWriter();
		writer.append("OK");
		writer.close();
		
	}
	
}
