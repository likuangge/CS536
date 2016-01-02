////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Title:            P1
// Semester:         CS 536 - Fall 2015
//
// Author:           Dan Wortmann
// Email:            dwortmann@wisc.edu
// CS Login:         dwortman
// Lecturer's Name:  Aws Albarghouthi
// import //

// notes //

public class Sym
{
    // Class Variables //
    private String symType;
    
    /*
     *  Constructor, initialize the Sym to the given type.
     *
     *  @param type - the type for the given Sym
     */
    public Sym(String type)
    {
        this.symType = type;
    }
    
    /*
     *  Return the Sym's type
     *
     *  @return type - the type for the given Sym
     */
    public String getType()
    {
        return symType;
    }

    /*
     *  Return the Sym's type
     *
     *  @return stringSym - string representation of the Sym class
     */
    public String toString()
    {
        String stringSym = "";

        // Construct the class string //
        stringSym = symType;
        
        return symType;
    }
}
