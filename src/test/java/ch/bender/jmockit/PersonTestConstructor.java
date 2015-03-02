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

import static org.junit.Assert.assertNull;
import mockit.Mock;
import mockit.MockUp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * TODO Hey Heri, comment this type please !
 *
 * @author Heri
 */
public class PersonTestConstructor
{
    
    /** logger for this class */
    private Logger myLog = LogManager.getLogger( PersonTestConstructor.class);

    @Test
    public void testGetName()
    {
        new MockUp<Person>()
        {
            @Mock
            public void $init()
            {
                // Dont assign name variable at all
                // Leave it null
            }

        };

        Person p = new Person();
        String name = p.getName();

        assertNull( "Name of person is null", name );
    }
    
    @Test
    public void testGetName2()
    {
        new MockUp<Person>()
        {
            @Mock
            public void $init( String name )
            {
                // Dont assign name variable at all
                // Leave it null
            }

        };

        Person p = new Person( "AbhiJMockit" );
        String name = p.getName();
        myLog.debug( "name: " + name );
        System.out.println( name );
        assertNull( "Name of person is null", name );
    }

}
