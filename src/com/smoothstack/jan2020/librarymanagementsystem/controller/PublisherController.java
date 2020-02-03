package com.smoothstack.jan2020.librarymanagementsystem.controller;

import com.smoothstack.jan2020.librarymanagementsystem.Repository.PublisherRepository;
import com.smoothstack.jan2020.librarymanagementsystem.Repository.Repository;
import com.smoothstack.jan2020.librarymanagementsystem.model.Publisher;
import com.smoothstack.jan2020.librarymanagementsystem.templates.Menu;
import com.smoothstack.jan2020.librarymanagementsystem.templates.MenuItem;

import java.util.*;
import java.util.function.Predicate;

public class PublisherController implements Controller {

    PublisherRepository publisherRepository = (PublisherRepository) Repository.getRepository(Publisher.class);

    public String requestMapping(String endPoint, Properties model, Properties requestParam) {
        try {
            switch(endPoint) {
                case "publisherMenu" :
                    return Objects.requireNonNull(publisherMenu(model, requestParam));
                case "processPublisherMenu" :
                    return  Objects.requireNonNull(processPublisherMenu(model, requestParam));
                case "addPublisher" :
                    return Objects.requireNonNull(addPublisher(model, requestParam));
                case "updatePublisher" :
                    return Objects.requireNonNull(updatePublisher(model, requestParam));
                case "deletePublisher" :
                    return Objects.requireNonNull(deletePublisher(model, requestParam));
                case "readAllPublisher" :
                    return Objects.requireNonNull(readAllPublisher(model, requestParam));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String publisherMenu(Properties model, Properties requestParam) {
        Menu menu = new Menu();

        menu.setBanner("Publisher Services");
        menu.getMenuItemMap().put(1L, new MenuItem(menu, "Add Publisher"));
        menu.getMenuItemMap().put(2L, new MenuItem(menu, "Delete Publisher"));
        menu.getMenuItemMap().put(3L, new MenuItem(menu, "Update Publisher"));
        menu.getMenuItemMap().put(4L, new MenuItem(menu, "Read All Publishers"));
        menu.getMenuItemMap().put(5L, new MenuItem(menu, "Quit to Previous Menu"));


        Optional.ofNullable(requestParam.getProperty("error")).ifPresent(error->model.setProperty("error", error));
        Optional.ofNullable(requestParam.getProperty("info")).ifPresent(error->model.setProperty("info", error));
        model.setProperty("menu", menu.toString());
        model.setProperty("prompt", "Select > ");
        model.setProperty("callback", "processPublisherMenu");
        return "display_menu";
    }

    public String processPublisherMenu(Properties model, Properties requestParam) {

        switch(Objects.requireNonNull(requestParam.getProperty("choice"))) {
            case "1": model.setProperty("redirectEndPoint", "addPublisher");
                break;

            case "2": model.setProperty("redirectEndPoint", "deletePublisher");
                break;

            case "3": model.setProperty("redirectEndPoint", "updatePublisher");
                break;

            case "4": model.setProperty("redirectEndPoint", "readAllPublisher");
                break;

            case "5": model.setProperty("redirectEndPoint", "mainMenu");
                break;

            default:
                model.setProperty("error", "Incorrect choice selected");
                model.setProperty("redirectEndPoint", "home");

        }

        return "redirect";
    }

    public String addPublisher(Properties model, Properties requestParam) {
        Optional<String> input = Optional.ofNullable(requestParam.getProperty("input"));
        Optional<String> publisherName = Optional.ofNullable(requestParam.getProperty("publisherName"));

        if (!publisherName.isPresent()) {
            if (!input.isPresent()) {
                model.setProperty("prompt", "Publisher Name >");
                model.setProperty("callback", "addPublisher");
                return "string_input";
            } else {
                String name = input.get();
                Iterator<Publisher> publishers = publisherRepository.find(publisher -> publisher.getName().toLowerCase().contains(name.toLowerCase()));

                if (publishers.hasNext()) {
                    StringBuilder sb = new StringBuilder("List of all similar publisher(s)");

                    sb.append("\n");
                    publishers.forEachRemaining(publisher -> sb.append("\t* ").append(publisher.getName()).append("\n"));
                    sb.append(String.format("\nNew Publisher '%s' \n", input.get()));;
                    model.setProperty("banner", sb.toString());
                } else {
                    model.setProperty("banner", String.format("New Publisher '%s' \n", input.get()));
                }

                model.setProperty("publisherName", input.get());
                model.setProperty("prompt", "Save? (y = save, n = abort) > ");
                model.setProperty("callback", "addPublisher");
                return "yesNo";
            }
        } else  {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "publisherMenu");
                return "redirect";
            } else if ("y".equals(input.get())) {


                publisherRepository.save(new Publisher(publisherName.get()));

                model.setProperty("info", "Saved\n");
                model.setProperty("redirectEndPoint", "publisherMenu");
                return "redirect";
            } else {
                model.setProperty("error", "Operation was aborted by user\n");
                model.setProperty("redirectEndPoint", "publisherMenu");
                return "redirect";
            }
        }

    }

    public String updatePublisher(Properties model, Properties requestParam) {
        Optional<String> input = Optional.ofNullable(requestParam.getProperty("input"));
        Optional<String> publisherId = Optional.ofNullable(requestParam.getProperty("publisherId"));
        Optional<String> publisherName = Optional.ofNullable(requestParam.getProperty("publisherName"));

        if (!publisherId.isPresent()) {

            if (!input.isPresent()) {
                model.setProperty("banner", "Modify Publisher, please enter Publisher's Id");
                model.setProperty("prompt", "Publisher's Id > ");
                model.setProperty("callback", "updatePublisher");
                return "string_input";
            } else {
                try {
                    long id = Long.parseLong(input.get());

                    Publisher publisher = publisherRepository.get(id);

                    model.setProperty("default", publisher.getName());
                    model.setProperty("prompt", "New Name >");
                    model.setProperty("publisherId", input.get());
                    model.setProperty("callback", "updatePublisher");
                    return "string_input";

                } catch (NumberFormatException e) {
                    model.setProperty("error", "Please enter Publisher's Id as a number.");
                    return "string_input";
                } catch (NoSuchElementException e) {
                    model.setProperty("error", "Publisher id not found in database.");
                    model.setProperty("redirectEndPoint", "publisherMenu");
                    return "redirect";
                }
            }
        } else if (!publisherName.isPresent()) {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "publisherMenu");
                return "redirect";
            } else {
                long id = Long.parseLong(publisherId.get());
                Publisher publisher = publisherRepository.get(id);

                model.setProperty("banner", String.format("\n\tcurrent name = '%s'\n\n\tnew name = '%s'\n",
                        publisher.getName(), input.get()));
                model.setProperty("publisherId", publisherId.get());
                model.setProperty("publisherName", input.get());
                model.setProperty("prompt", "Update (y = update, n = abort) > ");
                model.setProperty("callback", "updatePublisher");
                return "yesNo";
            }
        } else  {
            if (!input.isPresent()) {
                model.setProperty("error", "Internal Error");
                model.setProperty("redirectEndPoint", "publisherMenu");
                return "redirect";
            } else if ("y".equals(input.get())) {
                long id = Long.parseLong(publisherId.get());
                Publisher publisher = publisherRepository.get(id);
                publisher.setName(publisherName.get());

                publisherRepository.save(publisher);

                model.setProperty("info", "Modified\n");
                model.setProperty("redirectEndPoint", "publisherMenu");
                return "redirect";
            } else {
                model.setProperty("error", "Operation was aborted by user\n");
                model.setProperty("redirectEndPoint", "publisherMenu");
                return "redirect";
            }
        }

    }


    public String deletePublisher(Properties model, Properties requestParam) {
        Optional<String> publisherId = Optional.ofNullable(requestParam.getProperty("publisherId"));
        Optional<String> input = Optional.ofNullable(requestParam.getProperty("input"));

        model.setProperty("banner", "Please enter Publisher's Id to DELETE!");
        model.setProperty("prompt", "Publisher's Id > ");
        model.setProperty("callback", "deletePublisher");

        if (publisherId.isPresent()) {

            // Confirmed
            if ("y".equalsIgnoreCase(input.orElse("n"))) {

                try {
                    long id = Long.parseLong(publisherId.get());

                    Publisher publisher = publisherRepository.delete(id);
                    model.setProperty("info", publisher.getName() + " was deleted!");
                    model.setProperty("redirectEndPoint", "publisherMenu");
                    return "redirect";
                } catch (NumberFormatException e) {
                    model.setProperty("error", "Please enter Publisher's Id as a number.");
                    return "string_input";
                } catch (NoSuchElementException e) {
                    model.setProperty("error", "Publisher id not found in database.");
                    model.setProperty("redirectEndPoint", "publisherMenu");
                    return "redirect";
                }
            } else {
                model.setProperty("error", "Operation was aborted by user");
                model.setProperty("redirectEndPoint", "publisherMenu");
                return "redirect";
            }

        }
        // Not Confirmed
        else if (input.isPresent()) {
            try {
                long id = Long.parseLong(input.get());
                Publisher publisher = publisherRepository.get(id);

                model.setProperty("banner", String.format("Delete the following Publisher ?\n\n\tid = %d\n\tname = '%s'\n"
                        ,publisher.getLongId(),publisher.getName()));
                model.setProperty("prompt", "y = delete, n = abort > ");

                // preserve input as publisherId
                model.setProperty("publisherId", input.get());
                model.setProperty("callback", "deletePublisher");
                return "yesNo";

            } catch (NumberFormatException e) {
                model.setProperty("error", "Please enter Publisher's Id as a number.");
                return "string_input";
            } catch (NoSuchElementException e) {
                model.setProperty("error", "Publisher id not found in database.");
                model.setProperty("redirectEndPoint", "publisherMenu");
                return "redirect";
            }
        }


        return "string_input";
    }

    public String readAllPublisher(Properties model, Properties requestParam) {

        // Sort by name, get all
        Menu menu = getPublisherList(Comparator.comparing(Publisher::getName), captureAll -> true);

        model.setProperty("info", String.format("Total %d publisher(s) found.", menu.getMenuItemMap().size()));
        model.setProperty("menu", menu.toString());
        model.setProperty("prompt", "[ Enter To Continue ] ");
        model.setProperty("callback", "publisherMenu");
        return "display_menu";
    }
    private Menu getPublisherList() {
        return getPublisherList(Comparator.comparingLong(Publisher::getLongId), captureAll -> true);
    }
    private Menu getPublisherList(Comparator<? super Publisher> comparator, Predicate<? super Publisher> filter) {
        Menu menu = new Menu();
        List<Publisher> allPublisher = new ArrayList<>();

        publisherRepository.find(filter).forEachRemaining(allPublisher::add);

        if (comparator != null)
            allPublisher.sort(comparator);

        allPublisher.forEach(publisher-> menu.getMenuItemMap().put(publisher.getLongId(), new MenuItem(menu, publisher.getName())));

        return menu;
    }
}
