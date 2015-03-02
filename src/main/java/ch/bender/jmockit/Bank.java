/**
 * Copyright (c) 2015 by the original author or authors. This code is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version. The
 * above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED
 * "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ch.bender.jmockit;

/**
 * TODO Hey Heri, comment this type please !
 *
 * @author Heri
 */
public class Bank
{
    static int balanceAmount;
    DBManager dbManager;

    // Static block begins
    static
    {
        updateBalance( 100);
    }

    public static void updateBalance( float balance )
    {
        balanceAmount += balance;
    }

    public String processAccount( int accountID )
    {
        // Some other code goes here

        String accountHolderName = dbManager.retrieveAccountHolderName( accountID);

        // some more processing code

        return accountHolderName;
    }

    public String makeConnection()
    {
        // some connection related code
        // goes here

        // call to static method
//        String conStr = DBManager2.getConnectionString();
        String conStr = "";

        // If the connection String
        // is anything other than
        // ORIGINAL return FAIL
        if ( conStr.equals( "ORIGINAL") )
        {
            return "SUCCESS";
        }
        else
        {
            return "FAIL";
        }
    }
}
