/**
 * Copyright (c) 2015 by the original author or authors.
 *
 * This code is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ch.bender.jmockit;

import static org.junit.Assert.assertEquals;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

import org.junit.Test;


/**
 * TODO
 *
 * @version 1.0 (26.02.2015)
 * @author Bender (ERGONOMICS)
 */
public class BankWithDbManagerTest
{
    @Tested
    Bank bank;

    @Injectable
    DBManager dbManager;
    
    /**
     * 
     */
    @Test
    public void testRetrieveAccountHolderName()
    {


        // Define the Expectations block here
        new Expectations()
        {
            {
                dbManager.retrieveAccountHolderName( 10);
                returns( "Abhi");
            }
        };

        String name = bank.processAccount( 10);

        assertEquals( "Account holder Name for A/C id 10 is 'Abhi' ", "Abhi", name);

    }
}
