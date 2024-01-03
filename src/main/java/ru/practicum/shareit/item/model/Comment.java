package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "comments")
@NamedEntityGraph(
        name = "comment-with-author",
        attributeNodes = {
                @NamedAttributeNode("author")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;

    @Column(name = "text")
    private String text;

    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) && Objects.equals(item, comment.item) && Objects.equals(author, comment.author) && Objects.equals(text, comment.text) && Objects.equals(created, comment.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, item, author, text, created);
    }
}
