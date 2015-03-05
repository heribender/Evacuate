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



package ch.bender.evacuate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO Hey Heri, comment this type please !
 *
 * @author Heri
 */
public class EvacuateMainRunner
{
    
    /** logger for this class */
    private static Logger myLog = LogManager.getLogger( EvacuateMainRunner.class );

    /**
     * Constructor
     *
     */
    public EvacuateMainRunner()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * main
     * <p>
     * @param aArgs
     */
    public static void main( String[] aArgs )
    {
        try
        {
            String[] args;
            EvacuateMain main;
            
            args = new String[] { "-e",
                                  "F:/Daten/Administration/rdiffScript/EvacuateExclude_C.txt",
                                  "C:/Users",
                                  "S:/Users",
                                  "T:/c_"
                                   };
            main = new EvacuateMain( args );
            myLog.info( "Starting for C:/users" );
            main.init();
            main.run();
            
            args = new String[] { "-e",
                                  "F:/Daten/Administration/rdiffScript/EvacuateExclude_F.txt",
                                  "F:/",
                                  "R:/",
                                  "T:/f_"
                                 };
            main = new EvacuateMain( args );
            myLog.info( "Starting for C:/users" );
            main.init();
            main.run();
            
            
            
            
            myLog.info( "Successfully terminated" );
        }
        catch ( Throwable e )
        {
            myLog.error( "Exception caught: ", e );
        }
    }

}
