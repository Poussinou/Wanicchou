package data.vocab.models;

import android.os.Parcelable;

import java.util.List;

import data.vocab.RelatedWordEntry;

public interface Search extends Parcelable {
    Vocabulary getVocabulary();
    List<RelatedWordEntry> getRelatedWords();
}
