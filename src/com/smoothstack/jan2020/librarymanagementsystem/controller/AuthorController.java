package com.smoothstack.jan2020.librarymanagementsystem.controller;

import com.smoothstack.jan2020.librarymanagementsystem.Repository.AuthorRepository;
import com.smoothstack.jan2020.librarymanagementsystem.Repository.BookRepository;
import com.smoothstack.jan2020.librarymanagementsystem.Repository.Repository;
import com.smoothstack.jan2020.librarymanagementsystem.model.Author;
import com.smoothstack.jan2020.librarymanagementsystem.model.Book;
import com.smoothstack.jan2020.librarymanagementsystem.templates.Menu;
import com.smoothstack.jan2020.librarymanagementsystem.templates.MenuItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;

public class AuthorController implements Controller {

    AuthorRepository authorRepository = (AuthorRepository) Repository.getRepository(Author.class);
    BookRepository bookRepository = (BookRepository) Repository.getRepository(Book.class);
    public String requestMapping(String endPoint, Properties model, Properties requestParam) {
        try {
            switch(endPoint) {
                case "authorMenu" :
                    return Objects.requireNonNull(authorMenu(model, requestParam));
                case "processAuthorMenu" :
                    return  Objects.requireNonNull(processAuthorMenu(model, requestParam));
                case "addAuthor" :
                    return Objects.requireNonNull(addAuthor(model, requestParam));
                case "updateAuthor" :
                    return Objects.requireNonNull(updateAuthor(model, requestParam));
                case "deleteAuthor" :
                    return Objects.requireNonNull(deleteAuthor(model, requestParam));
                case "readAllAuthor" :
                    return Objects.requireNonNull(readAllAuthor(model, requestParam));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String authorMenu(Properties model, Properties requestParam) {
        Menu menu = new Menu();

        menu.setBanner("Author Services");
        menu.getMenuItemMap().put(1L, new MenuItem(menu, "Add Author"));
        menu.getMenuItemMap().put(2L, new MenuItem(menu, "Delete Author"));
        menu.getMenuItemMap().put(3L, new MenuItem(menu, "Update Author"));
        menu.getMenuItemMap().put(4L, new MenuItem(menu, "Read All Authors"));
        menu.getMenuItemMap().put(5L, new MenuItem(menu, "Quit to Previous Menu"));


        Optional.ofNullable(requestParam.getProperty("error")).ifPresent(error->model.setProperty("error", error));
        Optional.ofNullable(requestParam.getProperty("info")).ifPresent(error->model.setProperty("info", error));
        model.setProperty("menu", menu.toString());
        model.setProperty("prompt", "Select > ");
        model.setProperty("callback", "processAuthorMenu");
        return "display_menu";
    }

    public String processAuthorMenu(Properties model, Properties requestParam) {

        switch(Objects.requireNonNull(requestParam.getProperty("choice"))) {
            case "1": model.setProperty("redirectEndPoint", "addAuthor");
                break;

            case "2": model.setProperty("redirectEndPoint", "deleteAuthor");
                break;

            case "3": model.setProperty("redirectEndPoint", "updateAuthor");
                break;

            case "4": model.setProperty("redirectEndPoint", "readAllAuthor");
                break;

            case "5": model.setProperty("redirectEndPoint", "mainMenu");
                break;

            default:
                model.setProperty("error", "Incorrect choice selected");
                model.setProperty("redirectEndPoint", "home");

        }

        return "redirect";
    }

    public String addAuthor(Properties model, Properties requestParam) {
        Optional<String> input = Optional.ofNullable(requestParam.getProperty("input"));
        Optional<String> authorName = Optional.ofNullable(requestParam.getProperty("authorName"));

        if (!authorName.isPresent()) {
            if (!input.isPresent()) {
                model.setProperty("prompt", "Author Name >");
                model.setProperty("callback", "addAuthor");
                return "string_input";
            } else {
                String name = input.get();
                Iterator<Author> authors = authorRepository.find(author -> author.getName().toLowerCase().contains(name.toLowerCase()));

                if (authors.hasNext()) {
                    StringBuilder sb = new StringBuilder("List of all similar author(s)");

                    sb.append("\n");
                    authors.forEachRemaining(author -> sb.append("\t* ").append(author.getName()).append("\n"));
                    sb.append(String.format("\nNew Author '%s' \n", input.get()));;
                    model.setProperty("banner", sb.toString());
                } else {
                    model.setProperty("banner", String.format("New Author '%s' \n", input.get()));
                }

                model.setProperty("authorName", input.get());
                model.setProperty("prompt", "Save? (y = save, n = abort) > ");
                model.setProperty("callback", "addAuthor");
                return "yesNo";
            }
        } else  {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "authorMenu");
                return "redirect";
            } else if ("y".equals(input.get())) {


                authorRepository.save(new Author(authorName.get()));

                model.setProperty("info", "Saved\n");
                model.setProperty("redirectEndPoint", "authorMenu");
                return "redirect";
            } else {
                model.setProperty("error", "Operation was aborted by user\n");
                model.setProperty("redirectEndPoint", "authorMenu");
                return "redirect";
            }
        }

    }

    public String updateAuthor(Properties model, Properties requestParam) {
        Optional<String> input = Optional.ofNullable(requestParam.getProperty("input"));
        Optional<String> authorId = Optional.ofNullable(requestParam.getProperty("authorId"));
        Optional<String> authorName = Optional.ofNullable(requestParam.getProperty("authorName"));

        if (!authorId.isPresent()) {

            if (!input.isPresent()) {
                model.setProperty("banner", "Modify Author, please enter Author's Id");
                model.setProperty("prompt", "Author's Id > ");
                model.setProperty("callback", "updateAuthor");
                return "string_input";
            } else {
                try {
                    long id = Long.parseLong(input.get());

                    Author author = authorRepository.get(id);

                    model.setProperty("default", author.getName());
                    model.setProperty("prompt", "New Name >");
                    model.setProperty("authorId", input.get());
                    model.setProperty("callback", "updateAuthor");
                    return "string_input";

                } catch (NumberFormatException e) {
                    model.setProperty("error", "Please enter Author's Id as a number.");
                    return "string_input";
                } catch (NoSuchElementException e) {
                    model.setProperty("error", "Author id not found in database.");
                    model.setProperty("redirectEndPoint", "authorMenu");
                    return "redirect";
                }
            }
        } else if (!authorName.isPresent()) {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "authorMenu");
                return "redirect";
            } else {
                long id = Long.parseLong(authorId.get());
                Author author = authorRepository.get(id);

                model.setProperty("banner", String.format("\n\tcurrent name = '%s'\n\n\tnew name = '%s'\n",
                        author.getName(), input.get()));
                model.setProperty("authorId", authorId.get());
                model.setProperty("authorName", input.get());
                model.setProperty("prompt", "Update (y = update, n = abort) > ");
                model.setProperty("callback", "updateAuthor");
                return "yesNo";
            }
        } else  {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "authorMenu");
                return "redirect";
            } else if ("y".equals(input.get())) {
                long id = Long.parseLong(authorId.get());
                Author author = authorRepository.get(id);
                author.setName(authorName.get());

                authorRepository.save(author);

                model.setProperty("info", "Modified\n");
                model.setProperty("redirectEndPoint", "authorMenu");
                return "redirect";
            } else {
                model.setProperty("error", "Operation was aborted by user\n");
                model.setProperty("redirectEndPoint", "authorMenu");
                return "redirect";
            }
        }

    }



    public String deleteAuthor(Properties model, Properties requestParam) {
        Optional<String> authorId = Optional.ofNullable(requestParam.getProperty("authorId"));
        Optional<String> input = Optional.ofNullable(requestParam.getProperty("input"));

        model.setProperty("banner", "Please enter Author's Id to DELETE!");
        model.setProperty("prompt", "Author's Id > ");
        model.setProperty("callback", "deleteAuthor");

        if (authorId.isPresent()) {

            // Confirmed
            if ("y".equalsIgnoreCase(input.orElse("n"))) {

                try {
                    long id = Long.parseLong(authorId.get());
                    Author author = authorRepository.get(id);
                    StringBuilder  sb = new StringBuilder();
                    bookRepository.find(book->book.getAuthor().getLongId()==author.getLongId())
                            .forEachRemaining(
                                    book-> {
                                        sb.append(String.format("Book id=%d, name=%s was deleted!\n",book.getLongId(), book.getName()));
                                        bookRepository.delete(book.getLongId()); } );
                    authorRepository.delete(id);
                    sb.append("Author name=").append(author.getName()).append(" was deleted!\n");
                    model.setProperty("info", sb.toString());
                    model.setProperty("redirectEndPoint", "authorMenu");
                    return "redirect";
                } catch (NumberFormatException e) {
                    model.setProperty("error", "Please enter Author's Id as a number.");
                    return "string_input";
                } catch (NoSuchElementException e) {
                    model.setProperty("error", "id not found in database.");
                    model.setProperty("redirectEndPoint", "authorMenu");
                    return "redirect";
                }
            } else {
                model.setProperty("error", "Operation was aborted by user");
                model.setProperty("redirectEndPoint", "authorMenu");
                return "redirect";
            }

        }
        // Not Confirmed
        else if (input.isPresent()) {
            try {
                long id = Long.parseLong(input.get());
                Author author = authorRepository.get(id);

                model.setProperty("banner", String.format("Delete the following Author ?\n\n\tid = %d\n\tname = '%s'\n"
                        ,author.getLongId(),author.getName()));
                model.setProperty("prompt", "y = delete, n = abort > ");

                // preserve input as authorId
                model.setProperty("authorId", input.get());
                model.setProperty("callback", "deleteAuthor");
                return "yesNo";

            } catch (NumberFormatException e) {
                model.setProperty("error", "Please enter Author's Id as a number.");
                return "string_input";
            } catch (NoSuchElementException e) {
                model.setProperty("error", "Author id not found in database.");
                model.setProperty("redirectEndPoint", "authorMenu");
                return "redirect";
            }
        }


        return "string_input";
    }

    public String readAllAuthor(Properties model, Properties requestParam) {

        // Sort by name, get all Author
        Menu menu = getAuthorList(Comparator.comparing(Author::getName), captureAll -> true);

        model.setProperty("info", String.format("Total %d author(s) found.", menu.getMenuItemMap().size()));
        model.setProperty("menu", menu.toString());
        model.setProperty("prompt", "[ Enter To Continue ] ");
        model.setProperty("callback", "authorMenu");
        return "display_menu";
    }
    private Menu getAuthorList() {
        return getAuthorList(Comparator.comparingLong(Author::getLongId), captureAll -> true);
    }
    private Menu getAuthorList(Comparator<? super Author> comparator, Predicate<? super Author> filter) {
        Menu menu = new Menu();
        List<Author> allAuthor = new ArrayList<>();

        authorRepository.find(filter).forEachRemaining(allAuthor::add);

        if (comparator != null)
            allAuthor.sort(comparator);

        allAuthor.forEach(author-> menu.getMenuItemMap().put(author.getLongId(), new MenuItem(menu, author.getName())));

        return menu;
    }
}
