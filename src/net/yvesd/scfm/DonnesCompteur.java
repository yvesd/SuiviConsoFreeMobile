package net.yvesd.scfm;

import android.os.Parcel;
import android.os.Parcelable;

public class DonnesCompteur implements Parcelable {

	private String texte;
	private Integer ressourceId;

	public DonnesCompteur(String texte) {
		super();
//		texte = texte.replace(oldChar, newChar)
		this.texte = texte;
	}
	
	

	public DonnesCompteur(String texte, Integer ressourceId) {
		this(texte);
		this.ressourceId = ressourceId;
	}



	public static final Parcelable.Creator<DonnesCompteur> CREATOR = new Parcelable.Creator<DonnesCompteur>() {
		@Override
		public DonnesCompteur createFromParcel(Parcel in) {
			Integer ressourceIds = (Integer) in.readValue(null);
			DonnesCompteur donnesCompteur = new DonnesCompteur(in.readString());
			donnesCompteur.setRessourceId(ressourceIds);
			return donnesCompteur;
		}

		@Override
		public DonnesCompteur[] newArray(int size) {
			DonnesCompteur[] donnesCompteur = new DonnesCompteur[size];
			for (int i = 0; i < size; i++) {
				donnesCompteur[i] = new DonnesCompteur("");
			}
			return donnesCompteur;
		}
	};

	public String getTexte() {
		return texte;
	}

	public void setTexte(String texte) {
		this.texte = texte;
	}

	public Integer getRessourceId() {
		return ressourceId;
	}

	public void setRessourceId(Integer ressourceId) {
		this.ressourceId = ressourceId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(ressourceId);
		dest.writeString(texte);
	}

	@Override
	public String toString() {
		return texte;
	}
}
