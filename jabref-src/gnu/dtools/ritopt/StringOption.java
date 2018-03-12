package gnu.dtools.ritopt;

/**
 * StringOption.java
 *
 * Version:
 *    $Id: StringOption.java 1318 2005-11-08 04:53:29Z mortenalver $
 */

/**
 * This class is used for options with String values. 
 *
 * <pre>
 * Copyright (C) Damian Ryan Eads, 2001. All Rights Reserved.
 *
 * ritopt is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * ritopt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ritopt; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * </pre>
 *
 * @author Damian Eads
 */

public class StringOption extends Option {

    /**
     * The value of this string option.
     */

    private String value = "";

    /**
     * Constructs a string option that is initially set to zero.
     */

    public StringOption() {
	this( "" );
    }

    /**
     * Constructs a string option by copying the string option passed.
     *
     * @param op     The character option to copy.
     */

    public StringOption( StringOption op ) {
	super( op );
	op.value = op.getValue();
    }

    /**
     * Constructs a string option initialized with the value passed.
     *
     * @param value    The initial value of this string option.
     */

    public StringOption( String value ) {
	this( value, null );
    }

    /**
     * Constructs a string option initialized with the value and
     * long option passed.
     *
     * @param value      The initial value of this string option.
     * @param longOption The long option associated with string option.
     */

    public StringOption( String value, String longOption ) {
	this( value, longOption, '\0' );
    }

    /**
     * Constructs a character option initialized with the value and
     * short option passed.
     *
     * @param value       The initial value of this string option.
     * @param shortOption The short option associated with this option.
     */

    public StringOption( String value, char shortOption ) {
	this( value, null, shortOption );
    }

    /**
     * Constructs a string option initialized with the value, short
     * and long option passed.
     *
     * @param shortOption The short option associated with this option.
     * @param longOption  The long option associated with this option.
     * @param value       The initial value of this string option.
     */

    public StringOption( String value, String longOption, char shortOption ) {
	super( longOption, shortOption );
	this.value = value;
    }

    /**
     * Return the value as an object.
     *
     * @return This value as an option.
     */

    public Object getObject() {
	return value;
    }

    /**
     * Modify this option based on a string representation.
     *
     * @param     value String representation of the object.
     * @exception OptionModificationException Thrown if an error occurs
     *                                  during modification of an option.
     */

    public void modify( String value ) throws OptionModificationException {
	this.value = value;
    }

    /**
     * Modify this option based on a string representation.
     *
     * @param     value String representation of the object.
     * @exception OptionModificationException Thrown if an error occurs
     *                                  during modification of an option.
     */

    public void setValue( String value ) throws OptionModificationException {
	modify( value );
    }

    /**
     * Modify this option using a string value.
     *
     * @param     value A string value.
     */

    public String getValue() {
	return value;
    }

    /**
     * Return this option as a string.
     *
     * @return This option as a string.
     */

    public String getStringValue() {
	return value;
    }

    /**
     * Returns the type name of this option. For an StringOption, "STRING"
     * is returned.
     *
     * @return The type name of this option.
     */

    public String getTypeName() {
	return "STRING";
    }

    /**
     * Returns a string representation of this object.
     *
     * @return A string representation of this object.
     */

    public String toString() {
	return value;
    }

}
