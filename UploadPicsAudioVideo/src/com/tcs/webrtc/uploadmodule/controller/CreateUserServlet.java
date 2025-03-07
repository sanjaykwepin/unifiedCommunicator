package com.tcs.webrtc.uploadmodule.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.tcs.webrtc.uploadmodule.Dao.CreateUserDao;
import com.tcs.webrtc.uploadmodule.bean.UserProfile;

/**
 * Servlet implementation class CreateUserServlet
 */

public class CreateUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateUserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String action=request.getParameter("action");
		if(action.equalsIgnoreCase("ViewList"))
		{
			ArrayList<UserProfile> profiles=null;
			profiles=new CreateUserDao().getListOfUsers();
			if(profiles==null)
			{
				return;
			}
			else
			{
				request.setAttribute("List", profiles);
				request.getRequestDispatcher("ViewListOfProfiles.jsp").forward(request, response);
			}
		}
	if(action.equalsIgnoreCase("getUser"))
		{
			String userId=request.getParameter("id");
			System.out.println("userID...."+userId);
			UserProfile obj=null;
			obj=(new CreateUserDao()).getUser(userId);
			if(obj==null)
			{
			
				/*request.setAttribute("error", "Sorry the requested user could not be fetched");*/
				/*request.getRequestDispatcher("noProfile.jsp").forward(request, response);*/
				//response.sendRedirect("profile.jsp");
				System.out.println(""+obj);
				String str=userId;
				str= str.substring(str.lastIndexOf("sip:")+4, str.lastIndexOf("@tcs.com"));
				System.out.println(" userid : "+ userId +"-----str :"+str);
				
				response.sendRedirect("http://localhost:8080/WebRTC_presentation/pageone/noProfile.jsp?displayName="+str+"&privateIdentity="+userId);
				/*request.getRequestDispatcher("profile.jsp?displayName="+str+"&privateIdentity="+userId).forward(request, response);*/

				
			}
			else
			{
				System.out.println("return success");
				request.setAttribute("user", obj);
				request.getRequestDispatcher("ViewUserProfile.jsp").forward(request, response);
			}
		}
		if(action.equalsIgnoreCase("getProfilePic"))
		{
			
			UserProfile person=new CreateUserDao().getUser(request.getParameter("id"));
			if(person==null)
			{
				System.out.println("No Record Found");
				return;
			}
			response.setContentType("image/jpeg");
			response.setContentLength(person.getProfilepic().length);
			response.getOutputStream().write(person.getProfilepic());
		}
		if(action.equalsIgnoreCase("audio"))
		{
			
			UserProfile person=new CreateUserDao().getUser(request.getParameter("id"));
			if(person==null)
			{
				System.out.println("No Record Found");
				return;
			}
				FileOutputStream fos=new FileOutputStream("F:/"+person.getAudiofile());
				fos.write(person.getFavaudio()); //MAKE A COPY OF THE AUDIO AT THE SPECIFIED LOCATION
				fos.flush();
				fos.close();
				response.setContentType("audio/mp3");
				response.setHeader("Content-Disposition", "attachment; filename=\"" +person.getAudiofile()+"\"");//FORCE DOWNLOAD AS AN ATTACHMENT
				response.setContentLength(person.getFavaudio().length);
				response.getOutputStream().write(person.getFavaudio());
				
		
		}
		
		if(action.equalsIgnoreCase("video"))
		{
			UserProfile person=new CreateUserDao().getUser(request.getParameter("id"));
			if(person==null)
			{
				System.out.println("No Record Found");
				return;
			}
				String filename=person.getVideofile();
				System.out.println(filename);
				FileOutputStream fos=new FileOutputStream("F:/"+person.getVideofile());
				fos.write(person.getFavvideo());//MAKE A COPY OF THE VIDEO AT THE SPECIFIED LOCATION
				fos.flush();
				fos.close();
				response.setContentType("video/mp4");
				response.setHeader("Content-Disposition", "attachment; filename=\"" +person.getVideofile()+"\"");//FORCE DOWNLOAD AS AN ATTACHMENT
				response.setContentLength(person.getFavvideo().length);
				response.getOutputStream().write(person.getFavvideo());
				
				
		
		}
		
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		System.out.println("Uploda audio video proj : Create user servlet : post ");
		int i=0;
		FileInputStream inputStream = null;
		//String action=request.getRequestURI().split("/")[request.getRequestURI().split("/").length-1];
		//System.out.println(action);
		//if(action.equalsIgnoreCase("AddUser"))
		//{
			boolean isMultipart=ServletFileUpload.isMultipartContent(request);
			if(!isMultipart)
			{
				System.out.println("This is not a multi-part request");
				return;
			}
			try
			{
				UserProfile user=new UserProfile();
				DiskFileItemFactory factory=new DiskFileItemFactory();
				ServletFileUpload upload=new ServletFileUpload(factory);
				@SuppressWarnings("unchecked")
				List<FileItem> items=upload.parseRequest(request);
				Iterator<FileItem> iterator=items.iterator();
				while(iterator.hasNext())    //reading the contents of the form
				{
					FileItem item=iterator.next();
					if(item.isFormField())
					{
						String name=item.getFieldName();
						System.out.println(" usernbame "+name);
						String value=item.getString();
						System.out.println(" value"+value);
						if(name.equalsIgnoreCase("name"))
							user.setName(value);
/*						if(name.equalsIgnoreCase("userid"))
							user.setId(value);*/
					}
					else
					{
						String name=item.getFieldName();
						if(name.equalsIgnoreCase("picture"))
						{
							
							user.setProfilepic(item.get());
							user.setPicfile(item.getName());
						}
						
					if(name.equalsIgnoreCase("picture")&& item.get().length==0)
						{
							System.out.println("when image is default ");
							//File image = new File("UploadPicsAudioVideo/images/thumb-pic.png"); 
							File image = new File("/home/altanai/altanaiworkspace3/UploadPicsAudioVideo/WebContent/images/thumb-pic.png");
							inputStream = new FileInputStream(image); 
							byte[] bytes = IOUtils.toByteArray(inputStream);
							user.setProfilepic(bytes);
							user.setPicfile("default");
						}
						if(name.equalsIgnoreCase("audio"))
						{
							user.setFavaudio(item.get());
							user.setAudiofile(item.getName());
						}
						if(name.equalsIgnoreCase("video"))
						{
							user.setFavvideo(item.get());
							user.setVideofile(item.getName());
						}
					}
							
				}
				// send the object to dao to be inserted into the database
				 i=new CreateUserDao().addUser(user);
				 String addResult="";
				 if(i>0)
				 {		
					 addResult="User added successfully";
					 System.out.println("The values were added Successfully");
				 }
				 else
					 addResult="Could not add the user";
				 request.setAttribute("msg",addResult);
				 request.getRequestDispatcher("CreateResult.jsp").forward(request, response);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
	}

//}
