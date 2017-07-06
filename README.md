# Webpage-Content-Analysis
Java program which identifies keywords and the main content of a webpage.

In order to determine what the main, relevant content on a webpage is, I took a two pronged approach, as there were two important areas I thought of while considering the problem.

1) The HTML tags in which you would expect to find good information, and that which web developers hoping to get page views will most pay attention to. For the purpose of this program, these are tags such as title and h1, and also meta tags, searching for fields such as keyword, description, author, etc.

2) The 'main text' of the page itself- an article if it's a news website, or information about a product on a store page.

The first category is a relatively simple thing to approach. Utilizing the jsoup (https://jsoup.org/) library to obtain and parse a URL input by the user, I used the API to select the important tags and meta fields, and then break up the results into individual words to analyze. Part of this analysis included searching for and removing stop words, a nice small listing of which I utilized from: http://xpo6.com/list-of-english-stop-words/ . 

The second category proved to be more difficult, but mostly in terms of what to do with the much larger quantities of data and how to filter it. My initial idea for analyzing this text was to find an API for an English dictionary, scan through the text, keeping all nouns or words which couldn't be found in the dictionary (proper nouns, most usually), and then proceeding to do an analysis of the appearance rates of all the nouns collected. If the page was well written and on topic, the most common nouns used would be mostly what I'm looking for (however, an exception to this was easily found on the Amazon page for the toaster, in which Coffee is a word with a high appearance rate).

I gave up on the dictionary idea because I felt part of speech classification fell underneath the 'What's not allowed' section of the assignment, and that as such, I could build a similar solution, losing out on some accuracy but without requiring an extra library or potentially leaving the bounds of the assignment. 

The approach I settled on for this category was the same idea as the dictionary lookup, but without the lookup itself. I parsed the text of the page, and created a map with <String,Integer> pairs (words and counts) to log unique words and how often they appear. After stop words are similarly pruned and punctuation dealt with, I assembled a set containing words which appear at least half as much as the most prevalent word on the page. This was a threshold I experimented with on various webpages, but in the end couldn't create an algorithm to determine an optimal threshold with the data I had. However, what I've thought about it is that the type of content and the general word variety play a lot into how this would need to be formed, and if I were to continue to develop this progam, I would analyse news websites differently from commerce sites or blogs.

The final result of the program is then a combined set of the information gained from the tag analysis and text analysis. 

_______________________________________________________________________________________________________________________________

