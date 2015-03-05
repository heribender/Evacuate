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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

/**
 * TODO Hey Heri, comment this type please !
 *
 * @author Heri
 */
public final class Testconstants
{
    public static final String TEST_SANDBOX_DIR_STR = "./testsandbox";
    public static Path ROOT_DIR = Paths.get( TEST_SANDBOX_DIR_STR );


    /**
     * Creates a new file with given name in given parent directory.
     * <p>
     * The path of the new file is written into the file.
     * <p>
     * 
     * @param aParent
     *        must be an existing directory       
     * @param aNewFileName
     *        the name of the new file
     * @return the path object of the new file
     * @throws IOException
     */
    public static Path createNewFile( Path aParent, String aNewFileName )
        throws IOException
    {
        if ( !Files.isDirectory( aParent ) )
        {
            throw new IllegalArgumentException( "Given parent is not a directory or does not exist" );
        }
        
        Path file1 = Paths.get( aParent.toString(), aNewFileName );
        Files.createFile( file1 );
        FileUtils.writeStringToFile( file1.toFile(), file1.toString() + "\n" );
        return file1;
    }

    /**
     * createNewFolder
     * <p>
     * @param aParent
     * @param aNewFolderStr
     * @return
     * @throws IOException
     */
    public static Path createNewFolder( Path aParent, String aNewFolderStr )
        throws IOException
    {
        Path orig = Paths.get( aParent.toString(), aNewFolderStr );
        Files.createDirectory( orig );
        return orig;
    }

    /**
     * Constructor
     *
     */
    private Testconstants()
    {
        super();
    }

}
