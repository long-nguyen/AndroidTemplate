/**
 * <Class purpose>
 * 
 * @author <author>, 
 * http://activeuser.co
 * ========================================================================
 * Copyright 2015 Active User Co.Ltd
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
 * 
 */
package com.company.template.models;

import android.os.Parcel;
import android.os.Parcelable;


public  class TemplateData implements Parcelable {
	public int id;
	public String name;
	
	public TemplateData() {
	};

	public TemplateData(Parcel in) {
		this.readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
	}

	private void readFromParcel(Parcel in) {
		id = in.readInt();
		name = in.readString();
	}

	public static final Parcelable.Creator<TemplateData> CREATOR = new Parcelable.Creator<TemplateData>() {
		@Override
		public TemplateData createFromParcel(Parcel in) {
			return new TemplateData(in);
		}

		@Override
		public TemplateData[] newArray(int size) {
			return new TemplateData[size];
		}
	};
}
