package br.edu.ifsp.tcc.gbarzagli.embrapa.share.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Representation of a post
 * 
 * @author <a href="mailTo:gabriel.barzagli@hotmail.com"> Gabriel Viseli Barzagli (gabriel.barzagli@hotmail.com) </a>
 */
@Entity
public class Post extends PersistedObject {

    @Column
    private String sender;

    @ManyToOne
    @JoinColumn(name = "fk_plant", referencedColumnName = "id")
    private Plant plant;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "post", cascade = CascadeType.ALL)
    private List<Image> images;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.ALL)
    private List<Diagnostic> diagnostics;

    public Post() {}

    public Post(String sender, Plant plant, List<Image> images) {
        this.sender = sender;
        this.plant = plant;
        this.images = images;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
    
    

    public List<Diagnostic> getDiagnostics() {
        if (diagnostics != null) {
            Collections.sort(diagnostics, new Comparator<Diagnostic>() {
                @Override
                public int compare(Diagnostic o1, Diagnostic o2) {
                    if (o1.getScore() == o2.getScore()) {
                        return 0;
                    } else {
                        if (o1.getScore() < o2.getScore()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }
            });
        }
        
        return diagnostics;
    }

    public void setDiagnostics(List<Diagnostic> diagnostics) {
        this.diagnostics = diagnostics;
    }

    @Override
    public String toString() {
        return "Post [sender=" + sender + ", plant=" + plant + "]";
    }



}
