package net.yvesd.scfm;

import android.os.Parcel;
import android.os.Parcelable;

public class DonnesCompteur implements Parcelable {

	private String texte;
	private Integer ressourceId;
	private String nomClePreference;
	private int couleurDefaut;

	public DonnesCompteur(String texte) {
		super();
		this.texte = texte;
	}

	public DonnesCompteur(String texte, Integer ressourceId) {
		this(texte);
		this.ressourceId = ressourceId;
	}

	public DonnesCompteur(String texte, Integer ressourceId,
			String nomClePreference, int couleurDefaut) {

		this(texte, ressourceId);
		this.nomClePreference = nomClePreference;
		this.couleurDefaut = couleurDefaut;
	}

	public String getNomClePreference() {
		return nomClePreference;
	}

	public void setNomClePreference(String nomClePreference) {
		this.nomClePreference = nomClePreference;
	}

	public static final Parcelable.Creator<DonnesCompteur> CREATOR = new Parcelable.Creator<DonnesCompteur>() {
		@Override
		public DonnesCompteur createFromParcel(Parcel in) {
			Integer ressourceIds = (Integer) in.readValue(null);
			String nomClePreference = in.readString();
			int couleurDefaut = in.readInt();

			DonnesCompteur donnesCompteur = new DonnesCompteur(in.readString());
			donnesCompteur.setRessourceId(ressourceIds);
			donnesCompteur.setNomClePreference(nomClePreference);
			donnesCompteur.setCouleurDefaut(couleurDefaut);
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
		dest.writeString(nomClePreference);
		dest.writeString(texte);
		dest.writeInt(couleurDefaut);
	}

	@Override
	public String toString() {
		return texte;
	}

	public int getCouleurDefaut() {
		return couleurDefaut;
	}

	public void setCouleurDefaut(int couleurDefaut) {
		this.couleurDefaut = couleurDefaut;
	}
}
