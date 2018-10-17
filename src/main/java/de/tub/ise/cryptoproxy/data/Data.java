/*
 * Copyright 2018 Information Systems Engineering, TU Berlin, Germany
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.This is being developed for the DITAS Project: https://www.ditas-project.eu/
 */

package de.tub.ise.cryptoproxy.data;

/**
 * Wrapper class for a String since Spring-Boot sends data as a JSON
 * 
 * @author Paavo.Camps
 */
public class Data {
	private String data;

	public Data(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
