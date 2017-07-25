package introsde.localdatabase.model;

import introsde.localdatabase.dao.LifeCoachDao;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name="\"Activity\"")
@NamedQuery(name="Activity.findAll", query="SELECT a FROM Activity a")
@XmlRootElement
public class Activity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	// For sqlite in particular, you need to use the following @GeneratedValue annotation
	// This holds also for the other tables
	// SQLITE implements auto increment ids through named sequences that are stored in a 
	// special table named "sqlite_sequence"
	@GeneratedValue(generator="sqlite_activity")
	@TableGenerator(name="sqlite_activity", table="sqlite_sequence",
	    pkColumnName="name", valueColumnName="seq",
	    pkColumnValue="Activity")
	@Column(name="\"idActivity\"")
	private Long idActivity;

	@Column(name="\"name\"")
	private String name;
	
	@Temporal(TemporalType.DATE)
	@Column(name="\"date\"")
	private Date date;

	@ManyToOne
	@JoinColumn(name="idPerson",referencedColumnName="idPerson")
	private Person person;
	
	public Activity() {
	}
	
	public String getDate() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        // Get the date today using Calendar object.
        if (date == null) {
            return null;
        }
        return df.format(date);
    }

	public void setDate(String bd) throws ParseException {
		if (bd == null) {
			this.date = null;
			return;
		}
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = format.parse(bd);
        this.date = date;
    }
	
	// GETTERS AND SETTERS
	
	public Long getIdActivity() {
		return idActivity;
	}

	public void setIdActivity(Long idActivity) {
		this.idActivity = idActivity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// we make this transient for JAXB to avoid and infinite loop on serialization
	@XmlTransient
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
    
	// Database operations
	// Notice that, for this example, we create and destroy and entityManager on each operation. 
	// How would you change the DAO to not having to create the entity manager every time? 
	public static Activity getActivityById(Long activityId) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		Activity a = em.find(Activity.class, activityId);
		LifeCoachDao.instance.closeConnections(em);
		return a;
	}
	
	public static List<Activity> getAll() {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
	    List<Activity> list = em.createNamedQuery("Activity.findAll", Activity.class).getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    return list;
	}
	
	public static List<Activity> getPersonActivity(Long personId) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		
		List<Activity> list = em.createQuery(
                "SELECT m FROM Activity m WHERE m.person.idPerson = :id",
                Activity.class)
                .setParameter("id", personId)
                .getResultList();
		LifeCoachDao.instance.closeConnections(em);
		return list;
	}
	
	public static Activity saveActivity(Activity a) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(a);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return a;
	}
	
	public static Activity updateActivity(Activity a) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		a=em.merge(a);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return a;
	}
	
	public static void removeActivity(Activity a) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
	    a=em.merge(a);
	    em.remove(a);
	    tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	}
}

