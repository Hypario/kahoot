SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;


CREATE TABLE `proposition` (
  `idProposition` int(11) NOT NULL COMMENT 'Identifiant de la proposition',
  `text_proposition` varchar(255) NOT NULL COMMENT 'Texte de la proposition'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `question` (
  `idQuestion` int(11) NOT NULL COMMENT 'Identifiant de la question',
  `text_question` varchar(255) NOT NULL COMMENT 'Texte de la question',
  `annecdote` varchar(255) NOT NULL COMMENT 'Annecdote de la question',
  `rep_id` int(11) NOT NULL COMMENT 'Identifiant de la bonne réponse'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `question_proposition` (
  `QuestionId` int(11) NOT NULL COMMENT 'Identifiant de la Question',
  `PropositionId` int(11) NOT NULL COMMENT 'Identifiant de la proposition'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `question_quizz` (
  `idQuiz` int(11) NOT NULL COMMENT 'ID Quiz',
  `idQuestion` int(11) NOT NULL COMMENT 'ID Question',
  `idDifficulte` int(11) NOT NULL COMMENT 'Difficulté de la question'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `quiz` (
  `idQuiz` int(11) NOT NULL COMMENT 'Identifiant du Quiz',
  `author` varchar(255) NOT NULL COMMENT 'Auteur du Quiz',
  `theme` varchar(255) NOT NULL COMMENT 'Thème du Quiz',
  `difficulty` double NOT NULL COMMENT 'Difficulté du Quizz'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `type_question` (
  `type_id` int(11) NOT NULL COMMENT 'Type de la question',
  `label` varchar(255) NOT NULL COMMENT 'Label du type de question',
  `quizz_id` int(11) NOT NULL COMMENT 'Identifiant du quiz concerné'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


ALTER TABLE `proposition`
  ADD PRIMARY KEY (`idProposition`);

ALTER TABLE `question`
  ADD PRIMARY KEY (`idQuestion`),
  ADD KEY `rep_id` (`rep_id`);

ALTER TABLE `question_proposition`
  ADD PRIMARY KEY (`QuestionId`,`PropositionId`),
  ADD KEY `PropositionId` (`PropositionId`);

ALTER TABLE `question_quizz`
  ADD PRIMARY KEY (`idQuiz`,`idQuestion`),
  ADD KEY `idQuestion` (`idQuestion`),
  ADD KEY `idDifficulte` (`idDifficulte`);

ALTER TABLE `quiz`
  ADD PRIMARY KEY (`idQuiz`);

ALTER TABLE `type_question`
  ADD PRIMARY KEY (`type_id`),
  ADD KEY `quizz_id` (`quizz_id`);


ALTER TABLE `proposition`
  MODIFY `idProposition` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identifiant de la proposition';

ALTER TABLE `question`
  MODIFY `idQuestion` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identifiant de la question';

ALTER TABLE `quiz`
  MODIFY `idQuiz` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identifiant du Quiz';

ALTER TABLE `type_question`
  MODIFY `type_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Type de la question';


ALTER TABLE `question`
  ADD CONSTRAINT `question_ibfk_1` FOREIGN KEY (`rep_id`) REFERENCES `proposition` (`idProposition`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `question_proposition`
  ADD CONSTRAINT `question_proposition_ibfk_1` FOREIGN KEY (`QuestionId`) REFERENCES `question` (`idQuestion`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `question_proposition_ibfk_2` FOREIGN KEY (`PropositionId`) REFERENCES `proposition` (`idProposition`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `question_quizz`
  ADD CONSTRAINT `question_quizz_ibfk_1` FOREIGN KEY (`idQuestion`) REFERENCES `question` (`idQuestion`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `question_quizz_ibfk_2` FOREIGN KEY (`idQuiz`) REFERENCES `quiz` (`idQuiz`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `question_quizz_ibfk_3` FOREIGN KEY (`idDifficulte`) REFERENCES `type_question` (`type_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `type_question`
  ADD CONSTRAINT `type_question_ibfk_1` FOREIGN KEY (`quizz_id`) REFERENCES `quiz` (`idQuiz`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
