package com.dbdao;

import java.sql.*;
import java.sql.Date;
import java.util.Collection;


import java.util.*;

import com.added.functions.DBconnectorV3;
import com.added.functions.SharingData;
import com.dao.interfaces.*;
import com.javabeans.*;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import ExeptionErrors.DaoExeption;
import ExeptionErrors.FiledErrorException;
import ExeptionErrors.LoginException;

/**
 * This is Company Database DAO Class.
 * Just impelemnts the methods from CompanyDAO in 'com.dao.interfaces' package. 
 * 
 * 
 * @author Raziel
 *
 */

public class CompanyDBDAO implements CompanyDAO {
	
	// Default Constructor.
	public CompanyDBDAO() {}
	
	@Override
	public boolean login(String compName, String password) throws DaoExeption  {
		ResultSet rs1 = null;
		Statement stat1 = null;
		
		boolean hasRows = false;
        try {
			String sqlName = "SELECT Comp_name, password FROM company WHERE "
					+ "Comp_name= '" + compName + "'" + " AND " + "password= '" 
					+ password + "'";
			stat1 = DBconnectorV3.getConnection().createStatement();
		    rs1 = stat1.executeQuery(sqlName);
		    rs1.next();
		    
			if (rs1.getRow() != 0) {
				hasRows = true;
			}

            } catch (SQLException | NullPointerException e) {
            	// The throw exception here is in the companyFacade.
    			throw new DaoExeption("Error: Company Login - FAILED");
            } // catch
        
        if(hasRows == true) {
        	return hasRows;
        }
        return hasRows;
        	} // login	
	
	@Override
	public void createCompany(Company company) throws DaoExeption{
		
		// check if the company not exist && if the instances are not empty
		if (existOrNotByName(company) == false) {
			if(company.getCompName().isEmpty() || company.getEmail().isEmpty() || company.getPassword().isEmpty()) {
				throw new DaoExeption("Error: Creating Company - FAILED (empty fields)");
			} // if - empty
			else {
				try {
					String sqlQuery = "INSERT INTO company (COMP_NAME, PASSWORD, EMAIL) VALUES(?,?,?)";
					PreparedStatement prep = DBconnectorV3.getConnection().prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
					
					// now we will put the in their places.
					prep.setString(1, company.getCompName());
					prep.setString(2, company.getPassword());
					prep.setString(3, company.getEmail());
				
					// after we "loaded" the columns, we will executeUpdate prep.
					prep.executeUpdate();

					// This 2 lines will make it run to the next null row. and line 3 will set the ID (next new row).
					ResultSet rs = prep.getGeneratedKeys();
					while(rs.next()) {
					company.setId(rs.getLong(1));
					} // while
				} // try 
				catch (SQLException e) {
					throw new DaoExeption("Error: Creating New Company - FAILED (something went wrong)");
				} // catch
			} // else - empty
		} // if - existOrNotByName
		else {
			throw new DaoExeption("Error: Creating Company - FAILED (Company is already exist in the DataBase)");
		} // else
				
		
	} // createCompany - Function
	
	@Override
	public Coupon createCoupon(Coupon coupon) throws DaoExeption{

		
		return coupon;

	} // createCoupon - function
	
	@Override
	public void removeCompany(Company company) throws DaoExeption{
		
		// check if the company exist
		if (existOrNotByID(company.getId()) == true) {
			removeMethod(company.getId());
		}
		else {
			throw new DaoExeption("Error: Removing Company - FAILED (Company is not exist in the DataBase)");
		} // else

	} // removeCompany - By ID - Function
	
	@Override
	public void updateCompany(Company company) throws DaoExeption{
		
		// check if the company exist
		if (existOrNotByID(company.getId()) == true) {
			try {
				String sqlUpdate = "UPDATE company SET Comp_name=?, password=?, email=? WHERE Comp_ID=?";
				PreparedStatement prep = DBconnectorV3.getConnection().prepareStatement (sqlUpdate);
				prep.setString(1, company.getCompName());
				prep.setString(2, company.getPassword());
				prep.setString(3, company.getEmail());
				prep.setLong(4, company.getId());
				
				prep.executeUpdate();
			    
				} catch (SQLException e) {
					throw new DaoExeption("Error: Updating Company - FAILED");
				}
		}
		else {
			throw new DaoExeption("Error: Updating Company - FAILED (Company is not exist in the DataBase)");
		} // else
	
	} // updateCompany - Function
	
	@Override
	public Company getCompany(long id) throws DaoExeption{
		Company company = new Company();
		company.setId(id);
		
		// check if the company exist
		if (existOrNotByID(company.getId()) == true) {
			
			String compName = null, email = null, password = null;
			
			try {

				String sqlSEL = "SELECT * FROM company WHERE Comp_ID= ?" ;
				PreparedStatement prep = DBconnectorV3.getConnection().prepareStatement(sqlSEL);
				prep.setLong(1, id);
				ResultSet rs = prep.executeQuery();
				while(rs.next()) {
				compName = rs.getString("Comp_name");
				email = rs.getString("Email");
				password = rs.getString("password");
				} // while

				company = new Company(id, compName, password, email);
			}
			catch (SQLException | FiledErrorException e) {
				throw new DaoExeption("Error: Getting Company By ID - FAILED");
			}
			return company;
			
		}
		else {
			throw new DaoExeption("Error: Getting Company - FAILED (Company is not exist in the DataBase)");
		} // else
		
		
		
	} // getCompany - Function

	@Override
    public Company getCompany(String compName) throws DaoExeption{
		
		Company company = new Company();
		
			try {
				// check if the company exist
				company.setCompName(compName);
				if (existOrNotByName(company) == true) {
					String email, password;
					long id;
				
				String sqlSEL = "SELECT * FROM company WHERE comp_name= ?";
				PreparedStatement prep = DBconnectorV3.getConnection().prepareStatement(sqlSEL);
				prep.setString(1, company.getCompName());
				ResultSet rs = prep.executeQuery();
				rs.next();
				id = rs.getLong("comp_id");
				email = rs.getString("Email");
				password = rs.getString("password");
				
				company = new Company(id, compName, password, email);
				}
				else {
					throw new DaoExeption("Error: Getting Company - FAILED (Company is not exist in the DataBase)");
				} // else
			}
			catch (SQLException | FiledErrorException e) {
				throw new DaoExeption("Error: Getting Company By ID - FAILED");
			}
			return company;
		
		
		
	}

	@Override
	public Collection<Company> getAllCompanies() throws DaoExeption{
		
		String sql = "SELECT * FROM company";
		Collection<Company> companies = new HashSet<>();
		Company c = null;
		ResultSet rs = null;
		
		try {
			
			Statement stat = DBconnectorV3.getConnection().createStatement();
			rs = stat.executeQuery(sql);
			
			while(rs.next()) {
				c = new Company();
				c.setId(rs.getLong("Comp_ID"));
				c.setCompName(rs.getString("Comp_name"));
				c.setPassword(rs.getString("password"));
				c.setEmail(rs.getString("email"));
				
				companies.add(c);
			} // while loop

		} catch (SQLException | FiledErrorException e) {
			throw new DaoExeption("Error: Getting all Companies - FAILED");
		} // catch

		return companies;
	} // getAllCompanies

	@Override
	public Collection<Coupon> getCoupons(long compID) throws DaoExeption{
		 		
		Set<Coupon> coupons = new HashSet<>();
		CouponDBDAO  couponDB = new CouponDBDAO();
		
		try {
			
			String sql = "SELECT Coup_ID FROM coupon WHERE Owner_ID=?";
			PreparedStatement stat = DBconnectorV3.getConnection().prepareStatement (sql);
			stat.setLong(1, compID);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				coupons.add(couponDB.getCoupon(rs.getLong("Coup_ID")));
			}	
		} catch (SQLException e) {
			throw new DaoExeption("Error: Getting Coupons By The Owner ID (Company ID) - FAILED" );
		}
		return coupons;
	}
	
	/* Here it's 3 pirvate methods. my add on to this class.
	*  the Two last methods 'exist' - can be in some public class.
	*  but all the work, like connection to database, and checks, and exception.. 
	*  all goes from here. from the DBDAO.
	*  so consequently I decieded to put this 3 methods here as private. in all the DBDAO (3 classes)
	*  and adjust this methods to the currently DBDAO class.  
	*/
	
	/**
	 * 
	 * This is a remove method - making the deleting of company more flexible and easy.
	 * It's my add on.
	 * 
	 * @param long id
	 * @param String table
	 * 
	 * @author Raziel
	 */
	private void removeMethod(long id) throws DaoExeption{
		//String compName, email, password;
		
		String sqlDELid1 = "DELETE FROM company WHERE Comp_ID =" + id;
		String sqlDELid2 = "DELETE FROM company_coupon WHERE Comp_ID =" + id;

		PreparedStatement prep;
		try {
			prep = DBconnectorV3.getConnection().prepareStatement(sqlDELid1);
			prep.executeUpdate();
			prep.clearBatch();
			prep = DBconnectorV3.getConnection().prepareStatement(sqlDELid2);
			prep.executeUpdate();
		} catch (SQLException e) {
			throw new DaoExeption("Error: Removing Company - FAILED");
		}
		
	} // removeMethod
	
    private boolean existOrNotByID(long id) throws DaoExeption {
//		// TODO: Why did I check the enum here? I know i need to split the createCoupon - one for the company and one for the customerDBDAO
//    	if(client == ClientTable.COMPANY) {
//    		
//    		boolean answer = false;
//    		try {
//        		
//        		String sqlName = "SELECT Comp_ID FROM company WHERE "
//        		+ "Comp_ID= " + "'" + id + "'";
//        		
//        		Statement stat = DBconnectorV3.getConnection().createStatement();
//        		ResultSet rs = stat.executeQuery(sqlName);
//    			rs.next();
//    					   
//    			if (rs.getRow() != 0) {
//    				answer = true;
//    			} // if
//    		} catch (SQLException e) {
//    			throw new DaoExeption("Error: cannot make sure if the company is in the DataBase");
//    		}
//    		return answer;
//    	}
//    	else if (client == ClientTable.COUPON) {
//    		boolean answer = false;
//    		try {
//        		
//        		String sqlName = "SELECT Comp_ID FROM company WHERE "
//        		+ "Comp_ID= " + "'" + id + "'";
//        		
//        		Statement stat = DBconnectorV3.getConnection().createStatement();
//        		ResultSet rs = stat.executeQuery(sqlName);
//    			rs.next();
//    					   
//    			if (rs.getRow() != 0) {
//    				answer = true;
//    			} // if
//    		} catch (SQLException e) {
//    			throw new DaoExeption("Error: cannot make sure if the company is in the DataBase");
//    		}
//    	}
		return false;
    	
		
	}

    private boolean existOrNotByName(Company company) throws DaoExeption {
		
 	    Statement stat = null;
 		ResultSet rs = null;
 		boolean answer = false;
 		   
 		  try {
 				String sqlName = "SELECT Comp_name FROM company WHERE "
 				+ "comp_name= '" + company.getCompName() + "'";
 				stat = DBconnectorV3.getConnection().createStatement();
 				rs = stat.executeQuery(sqlName);
 				rs.next();
 			   
 				if (rs.getRow() != 0) {
 					answer = true;
 				} // if
 	            } catch (SQLException e) {
 	 	   			throw new DaoExeption("Error: cannot make sure if the company is in the DataBase");
// 		        e.printStackTrace();
 	            } // catch
 		  return answer;
 	}
	
    private Coupon createCouponByCompany(Coupon coupon) throws DaoExeption{
		
		long id = -1;
		
		// creating ResultSet
		ResultSet rs = null;
		
		// check if the company exist
		if (existOrNotByID(coupon.getId()) == true) {
			try {
				String sqlQuery = "INSERT INTO coupon (Title, Start_Date, End_Date, " + 
				"Amount, Category, Message, Price, Image, Owner_ID)" + "VALUES(?,?,?,?,?,?,?,?,?)";	
				PreparedStatement prep = DBconnectorV3.getConnection().prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
				prep.setString(1, coupon.getTitle());
				prep.setDate(2, Date.valueOf(coupon.getStartDate()));
				prep.setDate(3, Date.valueOf(coupon.getEndDate()));
				prep.setInt(4, coupon.getAmount());
				prep.setString(5, coupon.getType().toString());
				prep.setString(6, coupon.getMessage());
				prep.setDouble(7, coupon.getPrice());
				prep.setString(8, coupon.getImage());
				prep.setLong(9, coupon.getOwnerID());
				
				prep.executeUpdate();
				rs = prep.getGeneratedKeys();
				rs.next();
				id = rs.getLong(1);
				coupon.setId(id);
				
				// 2. Adding the new CouponID to the COMPANY_COUPON TABLE.
				
				long compID = coupon.getOwnerID();
				String sqlQuery1 = "INSERT INTO company_coupon (Comp_ID ,Coup_ID) VALUES ("+ compID +  
					"," + coupon.getId() + ");";
				PreparedStatement prep1 = DBconnectorV3.getConnection().prepareStatement(sqlQuery1);
				prep1.executeUpdate();
				
			} catch (SQLException e) {
				e.printStackTrace();
			} // catch

			return coupon;
		}
		else {
				throw new DaoExeption("Error: Removing Company - FAILED (Company is not exist in the DataBase)");
		} // else
		
	}

}
