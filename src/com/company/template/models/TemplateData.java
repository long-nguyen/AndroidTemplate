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
