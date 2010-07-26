/*
 * Copyright 2009 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.ui.resource.stream;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Streams non-css resources like images, scripts
 */
public class DefaultResourceStreamer implements ResourceStreamer {

	public boolean isAppropriateStreamer(String mimeType) {
		return (mimeType != null && !mimeType.equals("text/css"));
	}

	public void stream(HttpServletRequest request, HttpServletResponse response, InputStream inputStream) throws IOException {
		ServletOutputStream outputStream = response.getOutputStream();
		int indice, tempIndice;
	    byte tempArr[];
	    byte mainArr[] = new byte[0];
	    byte byteArr[] = new byte[65535];
        
        for(indice = 0; (indice = inputStream.read(byteArr)) > 0;)  {
          tempIndice = mainArr.length + indice;
          tempArr = new byte[tempIndice];
          System.arraycopy(mainArr, 0, tempArr, 0, mainArr.length);
          System.arraycopy(byteArr, 0, tempArr, mainArr.length, indice);
          mainArr = tempArr;
        }

        outputStream.write(mainArr);
        outputStream.flush();
        outputStream.close();
	}

}
