package br.edu.ifsp.tcc.gbarzagli.embrapa.share.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Representation of a post
 * @author <a href="mailTo:gabriel.barzagli@hotmail.com"> Gabriel Viseli Barzagli (gabriel.barzagli@hotmail.com) </a>
 */
@Entity
public class Post {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String sender;
	
	@ManyToOne
	@JoinColumn(name="fk_plant", referencedColumnName="id")
	private Plant plant;
	
	private String image;

	protected Post() {}
	
	public Post(String sender, Plant plant, String image) {
		this.sender = sender;
		this.plant = plant;
		this.image = image;
	}

	@Override
	public String toString() {
		return "Post [sender=" + sender + ", plant=" + plant.toString() + ", image=" + image + "]";
	}
	
}
